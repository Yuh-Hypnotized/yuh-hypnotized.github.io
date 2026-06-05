#include <iostream>
#include <vector>
#include <string>

using namespace std;

struct status
{
    vector<double> num;
    vector<string> exprFrags;
    int numCount;
};

void recursiveSearchFunc(vector<string>& solutionExprs, status& currentState)
{
    if (currentState.numCount == 1) {
        if (currentState.num[0] == 24)
            solutionExprs.push_back(currentState.exprFrags[0]);
        return;
    }

    for (int i=0; i<currentState.numCount; i++) {
        for (int j=0; j<currentState.numCount; j++) {
            if (i == j) continue;
            status nextState;
            nextState.numCount = currentState.numCount - 1;
            for (int k=0; k<currentState.numCount; k++) {
                if (k != i && k != j) {
                    nextState.num.push_back(currentState.num[k]);
                    nextState.exprFrags.push_back(currentState.exprFrags[k]);
                }
            }

            status nextState_plus = nextState;
            nextState_plus.num.push_back(currentState.num[i] + currentState.num[j]);
            nextState_plus.exprFrags.push_back("(" + currentState.exprFrags[i] + " + " + currentState.exprFrags[j] + ")");
            recursiveSearchFunc(solutionExprs, nextState_plus);

            status nextState_minus = nextState;
            nextState_minus.num.push_back(currentState.num[i] - currentState.num[j]);
            nextState_minus.exprFrags.push_back("(" + currentState.exprFrags[i] + " - " + currentState.exprFrags[j] + ")");
            recursiveSearchFunc(solutionExprs, nextState_minus);

            status nextState_mult = nextState;
            nextState_mult.num.push_back(currentState.num[i] * currentState.num[j]);
            nextState_mult.exprFrags.push_back(currentState.exprFrags[i] + " * " + currentState.exprFrags[j]);
            recursiveSearchFunc(solutionExprs, nextState_mult);

            status nextState_div = nextState;
            nextState_div.num.push_back(currentState.num[i] / currentState.num[j]);
            nextState_div.exprFrags.push_back(currentState.exprFrags[i] + " / " + currentState.exprFrags[j]);
            recursiveSearchFunc(solutionExprs, nextState_div);
        }
    }

}


int main()
{
    cout<<"Enter 4 numbers from 1 to 13: ";
    double num1, num2, num3, num4;
    cin>>num1>>num2>>num3>>num4;
    vector<string> solutionExprs = {};

    status currentState = {{num1, num2, num3, num4}, 
    {to_string(int(num1)), to_string(int(num2)), to_string(int(num3)), to_string(int(num4))}, 4};

    recursiveSearchFunc(solutionExprs, currentState);

    if (solutionExprs.size() == 0) cout<<"No solution has been found!"<<endl;
    else {
        for (int i=0; i<solutionExprs.size(); i++)
            cout<<"Solution "<<i+1<<": "<<solutionExprs[i]<<endl;
    }

    int exit; cin>>exit;
    if (exit == 0) return 0;
    else cin>>exit;
}
