#include "searchstudentdialog.h"
#include "ui_searchstudentdialog.h"

#include <QFile>
#include <QDebug>
#include <QScreen>
#include <QPainter>
#include "searchstudentresultdialog.h"
#include "faileddialog.h"

SearchStudentDialog::SearchStudentDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::SearchStudentDialog)
{
    ui->setupUi(this);
    setWindowTitle("查询学生");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.4, screenHeight * 0.6);


    QFile file("Students.txt");
    file.open(QIODevice::ReadOnly | QIODevice::Text);
    QTextStream in(&file);

    QVector<QString> studentIDs; //储存所有学生的学号
    while (!in.atEnd())
    {
        QString line = in.readLine();
        QStringList lineSplit1 = line.split('\t');
        QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

        QStringList stuItems = lineSplit1Vec[0].split(' ');
        QVector<QString> stuItemsVec = QVector<QString>::fromList(stuItems);

        studentIDs.push_back(stuItems[0]);
    }

    file.close();

    for (auto id : studentIDs)
    {
        ui->comboBox->addItem(id);
    }
}

SearchStudentDialog::~SearchStudentDialog()
{
    delete ui;
}

//按下OK 检查学生是否存在 -> 若存在，弹出result窗口 若不存在，弹出操作失败按钮
void SearchStudentDialog::on_pushButton_clicked()
{
    QString input_stuID = ui->comboBox->currentText(); //输入的学号

    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<<file.errorString();
    }
    else //打开文件
    {
        QTextStream in(&file);

        bool isFound = false;
        while(!in.atEnd())
        {
            QString line = in.readLine(); //读一行

            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

            QStringList studentItems = lineSplit1Vec[0].split(' ');
            QVector<QString> studentItemsVec = QVector<QString>::fromList(studentItems);

            if (studentItemsVec[0] == input_stuID) //找到
            {
                isFound = true;
                SearchStudentResultDialog resultDialog(this);
                connect(this, &SearchStudentDialog::sendData, &resultDialog, &SearchStudentResultDialog::receiveData);
                emit sendData(line); //将这一行的数据发送给result dialog
                resultDialog.exec();
                break;
            }
        }

        if (isFound == false) //未找到
        {
            FailedDialog failed(this);
            failed.exec();
        }
        else
        {
            close();
        }
    }
}

//按下Cancel 退出
void SearchStudentDialog::on_pushButton_2_clicked()
{
    close();
}

void SearchStudentDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label->setGeometry(x*0.1, y*0.2, x*0.6, y*0.1);
    ui->comboBox->setGeometry(x*0.1, y*0.4, x*0.8, y*0.1);
    ui->pushButton->setGeometry(x*0.2, y*0.7, x*0.2, y*0.1);
    ui->pushButton_2->setGeometry(x*0.6, y*0.7, x*0.2, y*0.1);

    QFont fontLabel("黑体",x*0.02);
    QFont fontBox("宋体", x*0.013);

    ui->label->setFont(fontLabel);
    ui->comboBox->setFont(fontBox);
    ui->pushButton->setFont(fontBox);
    ui->pushButton_2->setFont(fontBox);

}


void SearchStudentDialog::paintEvent(QPaintEvent *event) {
    QDialog::paintEvent(event); // 调用基类的 paintEvent

    //创建QPainter对象
    QPainter painter(this);

    //加载背景图片
    QPixmap background("background.jpg");

    //设置背景透明度
    int alpha = 50; // 范围是0-255，0为完全透明，255为不透明
    QImage img = background.toImage().convertToFormat(QImage::Format_ARGB32);

    for (int y = 0; y < img.height(); ++y) {
        for (int x = 0; x < img.width(); ++x) {
            QColor color = QColor(img.pixel(x, y));
            color.setAlpha(alpha); // 设置透明度
            img.setPixel(x, y, color.rgba());
        }
    }
    //将处理后的图片转换回 QPixmap
    QPixmap newBackground = QPixmap::fromImage(img);
    //绘制背景
    painter.drawPixmap(0, 0, newBackground);
}
