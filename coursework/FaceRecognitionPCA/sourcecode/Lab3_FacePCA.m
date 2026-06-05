clc;
clear;
close all;

%% ================= 1. 加载 ORL 数据 =================
load('ORL_64x64.mat');   % 改成你下载的 mat 文件名

% 转置成每列一张图像
X = double(fea)';       % 1024x400
labels = gnd;           % 400x1

imgH = 64;
imgW = 64;
numPerson = 40;
numPerPerson = 10;
trainNum = 8;           % 每人训练样本数量
testNum = 2;            % 每人测试样本数量
k = 100;                 % PCA 主成分数

%% ================= 2. 划分训练/测试集 =================
trainData = [];
trainLabel = [];
testData = [];
testLabel = [];

for i = 1:numPerson
    idx = find(labels == i);
    trainData = [trainData X(:, idx(1:trainNum))];
    trainLabel = [trainLabel; labels(idx(1:trainNum))];

    testData = [testData X(:, idx(trainNum+1:end))];
    testLabel = [testLabel; labels(idx(trainNum+1:end))];
end

%% ================= 3. 均值中心化 =================
meanFace = mean(trainData, 2);
A = trainData - meanFace;

%% ================= 4. PCA / 特征脸 =================
L = A' * A;                   % 小样本技巧
[V, D] = eig(L);

eigValues = diag(D);
[~, idx] = sort(eigValues, 'descend');
V = V(:, idx);

eigFaces = A * V;             % 投影回高维空间
eigFaces = eigFaces(:, 1:k);

% 归一化特征脸
for i = 1:k
    eigFaces(:, i) = eigFaces(:, i) / norm(eigFaces(:, i));
end

%% ================= 5. 显示前 16 张特征脸 =================
figure('Name','特征脸');
for i = 1:min(16,k)
    subplot(4,4,i);
    img = reshape(eigFaces(:, i), imgH, imgW);
    img = (img - min(img(:))) / (max(img(:)) - min(img(:))); % 归一化到0-1
    imagesc(img);
    colormap(gray);
    axis off;
    title(['Eigenface ' num2str(i)]);
end
sgtitle('前 16 个特征脸');

%% ================= 6. 训练集投影 =================
trainFeature = eigFaces' * A;

%% ================= 7. 测试集投影 =================
B = testData - meanFace;
testFeature = eigFaces' * B;

%% ================= 8. 最近邻分类 =================
correct = 0;
numTest = size(testFeature,2);
predictedLabel = zeros(numTest,1);

for i = 1:numTest
    dist = vecnorm(trainFeature - testFeature(:,i));
    [~, minIdx] = min(dist);
    predictedLabel(i) = trainLabel(minIdx);

    if predictedLabel(i) == testLabel(i)
        correct = correct + 1;
    end
end

accuracy = correct / numTest * 100;
fprintf('整体识别准确率：%.2f%%\n', accuracy);

%% ================= 9. 可视化随机测试样本 =================
rng('shuffle'); % 每次随机
randIdx = randi(numTest);
testImg = testData(:, randIdx);
testLab = testLabel(randIdx);
predLab = predictedLabel(randIdx);

% 找匹配训练样本
dist = vecnorm(trainFeature - eigFaces'*(testImg - meanFace));
[~, matchIdx] = min(dist);
matchImg = trainData(:, matchIdx);

figure('Name','测试样本识别');
subplot(1,2,1);
imagesc(reshape(testImg,imgH,imgW));
colormap(gray); axis off;
title(['测试人脸 (真实: ' num2str(testLab) ')']);

subplot(1,2,2);
imagesc(reshape(matchImg,imgH,imgW));
colormap(gray); axis off;
title(['识别结果 (预测: ' num2str(predLab) ')']);
