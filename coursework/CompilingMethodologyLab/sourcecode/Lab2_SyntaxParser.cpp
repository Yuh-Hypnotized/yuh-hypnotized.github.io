#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <iomanip>
#include <sstream>

using namespace std;

vector<string> split(const string& line) {
    vector<string> tokens;
    string token;
    stringstream ss(line);
    while (ss >> token) {
        tokens.push_back(token);
    }
    return tokens;
}

string stackToString(const vector<string>& st) {
    string res;
    for (auto it = st.rbegin(); it != st.rend(); ++it) {
        res += *it + " ";
    }
    return res;
}

string inputToString(const vector<string>& input, int index) {
    string res;
    for (int i = index; i < input.size(); ++i) {
        res += input[i] + " ";
    }
    return res;
}

int main() {
    ifstream fin("syntax_input.txt");
    ofstream fout("syntax_output.txt");

    if (!fin.is_open()) {
        cout << "Cannot open syntax_input.txt\n";
        return 1;
    }

    string line;
    getline(fin, line);
    vector<string> input = split(line);
    input.push_back("$");

    // LL(1) parsing table
    map<pair<string, string>, vector<string>> table;

    table[{ "Program", "ID" }] = { "Stmt" };
    table[{ "Stmt", "ID" }] = { "ID", "ASSIGN", "Expr", "SEMI" };
    table[{ "Expr", "INT" }] = { "Term", "Expr'" };
    table[{ "Expr'", "PLUS" }] = { "PLUS", "Term", "Expr'" };
    table[{ "Expr'", "SEMI" }] = { "ε" };
    table[{ "Term", "INT" }] = { "INT" };

    vector<string> stack;
    stack.push_back("$");
    stack.push_back("Program");

    int ip = 0;

    fout << "---------------------------------------------------------------\n";
    fout << left << setw(30) << "Stack"
         << setw(30) << "Input"
         << "Action\n";
    fout << "---------------------------------------------------------------\n";

    while (!stack.empty()) {
        string top = stack.back();
        string cur = input[ip];

        fout << left << setw(30) << stackToString(stack)
             << setw(30) << inputToString(input, ip);

        if (top == cur && top == "$") {
            fout << "Parsing completed successfully\n";
            break;
        }

        if (top == cur) {
            fout << "match " << cur << "\n";
            stack.pop_back();
            ip++;
        } else if (table.count({ top, cur })) {
            vector<string> production = table[{ top, cur }];
            fout << top << " -> ";
            for (auto& s : production) fout << s << " ";
            fout << "\n";

            stack.pop_back();
            if (!(production.size() == 1 && production[0] == "ε")) {
                for (auto it = production.rbegin(); it != production.rend(); ++it) {
                    stack.push_back(*it);
                }
            }
        } else {
            fout << "Error: unexpected token " << cur << "\n";
            break;
        }
    }

    fout << "---------------------------------------------------------------\n";

    fin.close();
    fout.close();
    return 0;
}
