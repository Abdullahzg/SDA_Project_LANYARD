import re

def extract_functions_from_file(file_path):
    with open(file_path, 'r') as file:
        java_code = file.read()

    # Updated regex pattern to match only function signatures
    function_pattern = re.compile(r'^\s*(public|private|protected)?\s+(\w+)\s+(\w+)\(([^)]*)\)\s*{', re.MULTILINE)
    functions = function_pattern.findall(java_code)

    formatted_functions = []
    for access_modifier, return_type, function_name, params in functions:
        access_symbol = '+' if access_modifier == 'public' else '-'
        formatted_function = f"{access_symbol} {function_name}({params})"
        if return_type != 'void':
            formatted_function += f":{return_type}"
        formatted_functions.append(formatted_function)

    return formatted_functions

# Example usage
file_path = 'D:\sda\SDA_Project_LANYARD (jfx ver)\src\main\java\org\example\sda_frontend\controller\CryptoSystem.java'  # Replace with the path to your Java file
functions = extract_functions_from_file(file_path)
for function in functions:
    print(function)