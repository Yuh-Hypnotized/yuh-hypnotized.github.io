#include "studentlistdialog.h"
#include "ui_studentlistdialog.h"
#include <QTableWidget>
#include <QFile>
#include <QDebug>
#include <QTextStream>
#include <QVBoxLayout>
#include <QScreen>
#include <QPainter>


#include "searchstudentresultdialog.h"

StudentListDialog::StudentListDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::StudentListDialog)
{
    ui->setupUi(this);
    setWindowTitle("学生个人信息列表");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.6, screenHeight * 0.6);

    //设置表格信息

    ui->tableWidget->setColumnCount(7); // 7列表格
    //设置表头标题
    QStringList headerText;
    headerText<<"学号"<<"姓名"<<"班级"<<"电话"<<"生日"<<"住址"<<" ";
    ui->tableWidget->setHorizontalHeaderLabels(headerText);

    ui->tableWidget->setEditTriggers(QAbstractItemView::NoEditTriggers); //不允许编辑

    //从文件Students.txt中读取学生信息
    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<< file.errorString();
    }
    else
    {
        QTextStream in(&file);
        int index = 0;

        QVector< QVector<QString> > studentsInfo; //用于存储所有学生的信息
        while (!in.atEnd()) //统计学生数量，并将信息暂存到studentsInfo中
        {
            QString line = in.readLine();
            QStringList lineSplit1 = line.split('\t'); //先以/t分割，将学生个人信息和选课信息分开
            QVector <QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);
            QStringList studentItems = lineSplit1Vec[0].split(' '); //将学生个人信息以空格分割
            QVector <QString> studentItemsVec = QVector<QString>::fromList(studentItems);

            studentsInfo.push_back(studentItemsVec);
            index++;
        }
        //一共有index个学生，所以需要index行
        ui->tableWidget->setRowCount(index);

        //写入表格中 显示
        for (int row=0;row<index;row++)
        {
            for (int col=0;col<6;col++)
            {
                ui->tableWidget->setItem(row,col,new QTableWidgetItem(studentsInfo[row][col]));
            }
            ui->tableWidget->setItem(row,6,new QTableWidgetItem("查看"));
        }

    }

    connect(ui->tableWidget, &QTableWidget::cellClicked, this, &StudentListDialog::onItemClicked);
}

StudentListDialog::~StudentListDialog()
{
    delete ui;
}

//关闭
void StudentListDialog::on_pushButton_clicked()
{
    close();
}

void StudentListDialog::resizeEvent(QResizeEvent *event)
{
    //父类的保持
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->tableWidget->setGeometry(x*0.02, y*0.01, x*0.96, y*0.74);
    ui->pushButton->setGeometry(x*0.3, y*0.8, x*0.4, y*0.1);

    ui->tableWidget->setColumnWidth(0,x*0.1); //学号
    ui->tableWidget->setColumnWidth(1,x*0.1); //姓名
    ui->tableWidget->setColumnWidth(2,x*0.1); //班级
    ui->tableWidget->setColumnWidth(3,x*0.12); //电话
    ui->tableWidget->setColumnWidth(4,x*0.12); //生日
    ui->tableWidget->setColumnWidth(5,x*0.3); //住址
    ui->tableWidget->setColumnWidth(6,x*0.08); //查看
}


void StudentListDialog::paintEvent(QPaintEvent *event) {
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


void StudentListDialog::onItemClicked(int row, int column)
{
    if (column == 6)
    {
        QString studentData = "";
        studentData += ui->tableWidget->item(row,0)->text();
        for (int i=1; i<6; i++)
        {
            studentData += (" " + ui->tableWidget->item(row,i)->text());
        }

        QFile file("Students.txt");
        file.open(QIODevice::ReadOnly | QIODevice::Text);
        QTextStream in(&file);

        while (!in.atEnd())
        {
            QString line = in.readLine();
            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);
            if (studentData == lineSplit1Vec[0])
            {
                studentData = line; //读取完整的学生信息
                break;
            }
        }

        SearchStudentResultDialog stuInfoDialog(this);
        connect(this, &StudentListDialog::sendData, &stuInfoDialog, &SearchStudentResultDialog::receiveData);
        emit(sendData(studentData));
        stuInfoDialog.exec();
    }
}
