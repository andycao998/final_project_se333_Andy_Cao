from fastmcp import FastMCP
import xml.etree.ElementTree as ET
import subprocess
import os

mcp = FastMCP("Demo 🚀")

@mcp.tool
def add(a: int, b: int) -> int:
    """Add two numbers"""
    return a

@mcp.tool(
    name="parse_jacoco",
    description="Parses a JaCoCo XML coverage report and returns line and branch coverage as well as missed line and branch coverage."
)
def parse_jacoco(file_path: str) -> dict:
    """
    Parses a jacoco.xml file and returns detailed coverage info per class,
    including counts and exact lines/branches missed.
    """
    tree = ET.parse(file_path)
    root = tree.getroot()
    coverage = {}

    # Iterate over packages and classes
    for package in root.findall("package"):
        for clazz in package.findall("class"):
            class_name = f"{package.attrib['name']}.{clazz.attrib['name']}"
            
            # Initialize counts and missed lists
            line_covered = 0
            line_missed = 0
            branch_covered = 0
            branch_missed = 0
            missed_lines = []
            missed_branches = []

            # Extract overall LINE and BRANCH counters
            for counter in clazz.findall("counter"):
                ctype = counter.attrib.get("type")
                covered = int(counter.attrib.get("covered", 0))
                missed = int(counter.attrib.get("missed", 0))
                if ctype == "LINE":
                    line_covered = covered
                    line_missed = missed
                elif ctype == "BRANCH":
                    branch_covered = covered
                    branch_missed = missed

            # Parse individual lines to find which lines and branches were missed
            for line in clazz.findall("lines/line") + clazz.findall("line"):
                line_num = int(line.attrib.get("nr", 0))
                ci = int(line.attrib.get("ci", 0))  # covered instructions
                mi = int(line.attrib.get("mi", 0))  # missed instructions
                mb = int(line.attrib.get("mb", 0))  # missed branches
                cb = int(line.attrib.get("cb", 0))  # covered branches

                # Record missed lines
                if mi > 0:
                    missed_lines.append(line_num)

                # Record missed branches
                if mb > 0:
                    missed_branches.append(line_num)

            coverage[class_name] = {
                "lines": {
                    "covered": line_covered,
                    "missed": line_missed,
                    "missed_numbers": missed_lines
                },
                "branches": {
                    "covered": branch_covered,
                    "missed": branch_missed,
                    "missed_numbers": missed_branches
                }
            }

    return coverage

@mcp.tool(
    name="run_mutation_testing",
    description="Run PIT mutation testing on a Maven Java project and return the report location."
)
def run_mutation_testing(project_path: str):

    if not os.path.exists(project_path):
        return "Project path does not exist."

    cmd = "mvn org.pitest:pitest-maven:mutationCoverage"

    try:
        result = subprocess.run(
            cmd,
            cwd=project_path,
            capture_output=True,
            text=True,
            shell=True  # required on Windows
        )
    except FileNotFoundError:
        return "Maven executable not found. Make sure Maven is installed and on PATH."

    if result.returncode != 0:
        return f"Mutation testing failed:\n{result.stderr}"

    report_dir = os.path.join(project_path, "target", "pit-reports")
    if not os.path.exists(report_dir):
        return "Mutation testing ran successfully, but no report found."

    return f"Mutation testing completed. Report located at: {report_dir}"

if __name__ == "__main__":
    # IMPORTANT: Use SSE transport so VS Code can connect via URL.
    mcp.run(transport="sse")