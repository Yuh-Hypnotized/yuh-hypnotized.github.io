#include <iostream>
#include <vector>
#include <cmath>
#include <limits>

using namespace std;

struct Point {
    double x, y;
    int cluster;
};

double distance(const Point& a, const Point& b) {
    return sqrt((a.x - b.x) * (a.x - b.x) +
                (a.y - b.y) * (a.y - b.y));
}

int main() {
    // init
    vector<Point> points = {
        {0, 0, -1}, {3, 8, -1}, {2, 2, -1}, {1, 1, -1},
        {5, 3, -1}, {4, 8, -1}, {6, 3, -1}, {5, 4, -1},
        {6, 4, -1}, {7, 5, -1}
    };

    const int K = 2;

    // centers
    vector<Point> centers;
    for (int i=0; i<K; i++){
        centers.push_back(points[i]);
    }

    bool changed = true;
    int iteration = 0;

    while (changed) {
        changed = false;
        iteration++;

        // 样本分配
        for (auto& p : points) {
            double minDist = numeric_limits<double>::max();
            int bestCluster = -1;

            for (int i = 0; i < K; i++) {
                double d = distance(p, centers[i]);
                if (d < minDist) {
                    minDist = d;
                    bestCluster = i;
                }
            }

            if (p.cluster != bestCluster) {
                p.cluster = bestCluster;
                changed = true;
            }
        }

        // 更新聚类中心
        vector<double> sumX(K, 0), sumY(K, 0);
        vector<int> count(K, 0);

        for (const auto& p : points) {
            sumX[p.cluster] += p.x;
            sumY[p.cluster] += p.y;
            count[p.cluster]++;
        }

        for (int i = 0; i < K; i++) {
            centers[i].x = sumX[i] / count[i];
            centers[i].y = sumY[i] / count[i];
        }
    }

    // output
    cout << "迭代次数: " << iteration << endl;
    for (int i = 0; i < K; i++) {

        cout << "聚类 " << i + 1 << " 中心: ("
             << centers[i].x << ", "
             << centers[i].y << ")" << endl;
    }

    cout << "\n样本聚类结果:\n";
    for (int i = 0; i < points.size(); i++) {
        cout << "x" << i + 1 << " ("
             << points[i].x << ", "
             << points[i].y << ") -> 聚类 "
             << points[i].cluster + 1 << endl;
    }

    return 0;
}
