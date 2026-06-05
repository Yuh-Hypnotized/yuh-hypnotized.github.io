#include "displayclassstudentsdialog.h"
#include "ui_displayclassstudentsdialog.h"

#include <QFile>
#include <QDebug>
#include <QTabWidget>
#include <QPainter>
#include <QScreen>

struct simplifiedStu  //简化的 储存学生概要信息的容器
{
    QString s_StuID;
    QString s_StuName;
    QString s_StuClass;
    QString s_StuScore;
};

DisplayClassStudentsDialog::DisplayClassStudentsDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::DisplayClassStudentsDialog)
{
    ui->setupUi(this);
    setWindowTitle("课程信息");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.6, screenHeight * 0.6);
}

DisplayClassStudentsDialog::~DisplayClassStudentsDialog()
{
    delete ui;
}

//退出Exit
void DisplayClassStudentsDialog::on_pushButton_clicked()
{
    close();
}

//接受到信息
void DisplayClassStudentsDialog::receiveClassID(QString classID)
{
    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<<file.errorString();
    }
    else //打开文件
    {
        ui->label_classID->setText(classID); //显示课程ID

        QVector<simplifiedStu> classStudentsVec;

        QTextStream in(&file);
        while (!in.atEnd())
        {
            QString line = in.readLine();

            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

            //遍历该学生的选课 找找有没有选这门课程
            for (int i=1; i<lineSplit1Vec.size(); i++)
            {
                QStringList classItems = lineSplit1Vec[i].split(' ');
                QVector<QString> classItemsVec = QVector<QString>::fromList(classItems);

                if (classItemsVec[0] == classID) //找到了
                {
                    QStringList stuItems = lineSplit1Vec[0].split(' ');
                    QVector<QString> stuItemsVec = QVector<QString>::fromList(stuItems);

                    simplifiedStu t_stuData;
                    t_stuData.s_StuID = stuItemsVec[0];
                    t_stuData.s_StuName = stuItemsVec[1];
                    t_stuData.s_StuClass = stuItemsVec[2];
                    t_stuData.s_StuScore = classItemsVec[5];
                    classStudentsVec.push_back(t_stuData); //储存学生信息

                    ui->label_className->setText(classItemsVec[1]); //显示课程名字
                }
            }
        }
        file.close();

        //将classStudentsVec中的数据进行排序 （按照成绩降序）
        std::sort(classStudentsVec.begin(),classStudentsVec.end(),
                  [](const simplifiedStu& a, const simplifiedStu& b)
        {
            return a.s_StuScore.toFloat() > b.s_StuScore.toFloat();
        });

        /*
        for (int i=0; i<classStudentsVec.size()-1; i++)
        {
            for (int j=classStudentsVec.size()-1; j>i; j--)
            {
                //冒泡排序
                if (classStudentsVec[j].s_StuScore > classStudentsVec[j-1].s_StuScore)
                {
                    simplifiedStu temp = classStudentsVec[j];
                    classStudentsVec[j] = classStudentsVec[j-1];
                    classStudentsVec[j-1] = temp;
                }
            }
        }*/

        //将已排序的数据写入tableWidget中
        ui->tableWidget->setColumnCount(5);

        QStringList headerText;
        headerText<<"学号"<<"姓名"<<"班级"<<"成绩"<<"排名";
        ui->tableWidget->setHorizontalHeaderLabels(headerText);

        ui->tableWidget->setRowCount(classStudentsVec.size());

        for (int row=0; row<classStudentsVec.size(); row++)
        {
            ui->tableWidget->setItem(row,0,new QTableWidgetItem(classStudentsVec[row].s_StuID));
            ui->tableWidget->setItem(row,1,new QTableWidgetItem(classStudentsVec[row].s_StuName));
            ui->tableWidget->setItem(row,2,new QTableWidgetItem(classStudentsVec[row].s_StuClass));
            ui->tableWidget->setItem(row,3,new QTableWidgetItem(classStudentsVec[row].s_StuScore));
            ui->tableWidget->setItem(row,4,new QTableWidgetItem(QString::number(row+1)));
        }
    }
}

void DisplayClassStudentsDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label->setGeometry(x*0.1, y*0.05, x*0.3, y*0.05);
    ui->label_classID->setGeometry(x*0.4, y*0.05, x*0.6, y*0.05);
    ui->label_3->setGeometry(x*0.1, y*0.1, x*0.3, y*0.05);
    ui->label_className->setGeometry(x*0.4, y*0.1, x*0.6, y*0.05);

    ui->tableWidget->setGeometry(x*0.1, y*0.2, x*0.8, y*0.5);
    ui->pushButton->setGeometry(x*0.3, y*0.8, x*0.4, y*0.1);

    QFont leftFont("黑体", x*0.01);
    QFont rightFont("宋体", x*0.01);
    ui->label->setFont(leftFont);
    ui->label_3->setFont(leftFont);
    ui->label_classID->setFont(rightFont);
    ui->label_className->setFont(rightFont);

    ui->tableWidget->setColumnWidth(0,x*0.2);
    ui->tableWidget->setColumnWidth(1,x*0.2);
    ui->tableWidget->setColumnWidth(2,x*0.2);
    ui->tableWidget->setColumnWidth(3,x*0.1);
    ui->tableWidget->setColumnWidth(4,x*0.05);
}


void DisplayClassStudentsDialog::paintEvent(QPaintEvent *event) {
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
