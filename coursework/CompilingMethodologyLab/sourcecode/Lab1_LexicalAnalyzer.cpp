#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <unordered_set>
#include <cctype>

using namespace std;

struct Token {
    int line;
    int column;
    string type;
    string value;
};

unordered_set<string> keywords = {"if", "while"};

bool isLetter(char c) {
    return isalpha(c) || c == '_';
}

bool isDigit(char c) {
    return isdigit(c);
}

int main() {
    ifstream in("lex_input.txt");
    ofstream out("lex_output.txt");

    if (!in.is_open() || !out.is_open()) {
        cout << "File open error\n";
        return 1;
    }

    vector<string> lines;
    string line;
    while (getline(in, line)) {
        lines.push_back(line);
    }

    bool inBlockComment = false;
    int lineNum = 1;

    out << "Line\tColumn\tType\t\tValue\n";
    out << "----------------------------------------\n";

    for (const string& s : lines) {
        int i = 0;
        int n = s.size();

        while (i < n) {
            char c = s[i];

            /* 块注释中 */
            if (inBlockComment) {
                if (c == '*' && i + 1 < n && s[i + 1] == '/') {
                    inBlockComment = false;
                    i += 2;
                } else {
                    i++;
                }
                continue;
            }

            /* 空白 */
            if (isspace(c)) {
                i++;
                continue;
            }

            int col = i + 1;

            /* 单行注释 */
            if (c == '/' && i + 1 < n && s[i + 1] == '/') {
                break;
            }

            /* 块注释开始 */
            if (c == '/' && i + 1 < n && s[i + 1] == '*') {
                inBlockComment = true;
                i += 2;
                continue;
            }

            /* 标识符 / 关键字 */
            if (isLetter(c)) {
                string word;
                while (i < n && (isLetter(s[i]) || isDigit(s[i]))) {
                    word += s[i++];
                }
                if (keywords.count(word))
                    out << lineNum << "\t" << col << "\tKEYWORD\t\t" << word << "\n";
                else
                    out << lineNum << "\t" << col << "\tIDENTIFIER\t" << word << "\n";
                continue;
            }

            /* 整数 */
            if (isDigit(c)) {
                string num;
                while (i < n && isDigit(s[i])) {
                    num += s[i++];
                }
                out << lineNum << "\t" << col << "\tINT\t\t" << num << "\n";
                continue;
            }

            /* 双字符运算符 */
            if (i + 1 < n) {
                string two = s.substr(i, 2);
                if (two == "==") { out << lineNum << "\t" << col << "\tEQ\t\t==\n"; i += 2; continue; }
                if (two == "!=") { out << lineNum << "\t" << col << "\tNE\t\t!=\n"; i += 2; continue; }
                if (two == "<=") { out << lineNum << "\t" << col << "\tLE\t\t<=\n"; i += 2; continue; }
                if (two == ">=") { out << lineNum << "\t" << col << "\tGE\t\t>=\n"; i += 2; continue; }
            }

            /* 单字符 */
            switch (c) {
                case '=': out << lineNum << "\t" << col << "\tASSIGN\t\t=\n"; break;
                case '+': out << lineNum << "\t" << col << "\tPLUS\t\t+\n"; break;
                case '-': out << lineNum << "\t" << col << "\tMINUS\t\t-\n"; break;
                case '*': out << lineNum << "\t" << col << "\tMUL\t\t*\n"; break;
                case '/': out << lineNum << "\t" << col << "\tDIV\t\t/\n"; break;
                case '<': out << lineNum << "\t" << col << "\tLT\t\t<\n"; break;
                case '>': out << lineNum << "\t" << col << "\tGT\t\t>\n"; break;
                case '(': out << lineNum << "\t" << col << "\tLPAREN\t\t(\n"; break;
                case ')': out << lineNum << "\t" << col << "\tRPAREN\t\t)\n"; break;
                case '{': out << lineNum << "\t" << col << "\tLBRACE\t\t{\n"; break;
                case '}': out << lineNum << "\t" << col << "\tRBRACE\t\t}\n"; break;
                case ';': out << lineNum << "\t" << col << "\tSEMI\t\t;\n"; break;
                case ',': out << lineNum << "\t" << col << "\tCOMMA\t\t,\n"; break;
                default:
                    out << "Lexical Error at line " << lineNum
                        << ", column " << col
                        << ": illegal character '" << c << "'\n";
                    return 0;
            }
            i++;
        }
        lineNum++;
    }

    return 0;
}
