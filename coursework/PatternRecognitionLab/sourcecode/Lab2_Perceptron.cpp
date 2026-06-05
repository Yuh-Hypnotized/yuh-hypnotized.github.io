#include <iostream>
#include <vector>
using namespace std;

// dot prod
int dot(const vector<int>& w, const vector<int>& x) {
    int sum = 0;
    for (int i = 0; i < w.size(); i++)
        sum += w[i] * x[i];
    return sum;
}

void outputVec(const vector<int>& w) {
    cout << "[" << w[0];
    for (int i = 1; i<w.size(); i++) {
        cout << ", " << w[i];
    }
    cout<< "]" << endl;
}

int main() {
    vector<int> W = { -1, -2, -2, 0 };

    vector<vector<int>> X = {
        // ω1 
        {0, 0, 0,  1},
        {1, 0, 0,  1},
        {1, 0, 1,  1},
        {1, 1, 0,  1},

        // ω2 
        {0, 0, 1, -1},
        {0, 1, 1, -1},
        {0, 1, 0, -1},
        {1, 1, 1, -1},
        {2,20,1,-1}
    };

    const int c = 1; 
    bool hasError;
    int iter = 0;

    cout << "W(1) = ";
    int index = 2;
    outputVec(W);

    do {
        hasError = false;
        iter++;
        cout << "\nIteration " << iter << endl;

        for (auto& x : X) {
            int g = dot(W, x);
            if (g < 0) {  // err
                for (int i = 0; i < W.size(); i++) {
                    W[i] += c * x[i];
                }
                hasError = true;
            }
            cout << "W(" << index++ << ") = ";
            outputVec(W);
        }

    } while (hasError);

    cout << "\n\nTraining finished." << endl;
    cout << "Final W = ";
    outputVec(W);

    return 0;
}
