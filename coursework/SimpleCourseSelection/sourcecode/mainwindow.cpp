#include "mainwindow.h"
#include "ui_mainwindow.h"

#include "studentlistdialog.h"
#include "addstudentdialog.h"
#include "deletestudentdialog.h"
#include "searchstudentdialog.h"
#include "addstudentclassesdialog.h"
#include "deletestudentclassesdialog.h"
#include "classesdialog.h"
#include "classeslistdialog.h"

#include <QScreen>
#include <QFont>
#include <QGraphicsScene>
#include <QGraphicsPixmapItem>
#include <QPainter>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    setWindowTitle("学生选课管理系统");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.6, screenHeight * 0.6);

}

MainWindow::~MainWindow()
{
    delete ui;
}

//新增学生
void MainWindow::on_addStudent_clicked()
{
    AddStudentDialog addStuDialog(this);
    addStuDialog.exec();
}

//退出Exit
void MainWindow::on_pushButtonExit_clicked()
{
    close();
}

//学生信息列表
void MainWindow::on_studentsList_clicked()
{
    StudentListDialog stuList(this);
    stuList.exec();

}

//删除学生
void MainWindow::on_deleteStudent_clicked()
{
    DeleteStudentDialog deleteStuDialog(this);
    deleteStuDialog.exec();
}

//查询学生
void MainWindow::on_searchStu_clicked()
{
    SearchStudentDialog searchStuDialog(this);
    searchStuDialog.exec();
}

//增加学生的选课
void MainWindow::on_addStuClass_clicked()
{
    AddStudentClassesDialog addStuClaDialog(this);
    addStuClaDialog.exec();
}

//删除学生的某选课
void MainWindow::on_deleteStuClass_clicked()
{
    DeleteStudentClassesDialog deleteStuClaDialog(this);
    deleteStuClaDialog.exec();
}

//查询课程的学生及成绩排名
void MainWindow::on_searchClass_clicked()
{
    ClassesDialog classesDialog(this);
    classesDialog.exec();
}

//重载resize
void MainWindow::resizeEvent(QResizeEvent *event)
{
    //父类的保持
    QMainWindow::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    //调整控件位置及大小
    ui->studentsList->setGeometry(x*0.1, y*0.2, x*0.2, y*0.1);
    ui->classesList->setGeometry(x*0.4, y*0.2, x*0.2, y*0.1);

    ui->searchStu->setGeometry(x*0.7, y*0.3, x*0.2, y*0.1);
    ui->searchClass->setGeometry(x*0.7, y*0.5, x*0.2, y*0.1);

    ui->addStudent->setGeometry(x*0.1, y*0.4, x*0.2, y*0.1);
    ui->deleteStudent->setGeometry(x*0.4, y*0.4, x*0.2, y*0.1);

    ui->addStuClass->setGeometry(x*0.1, y*0.6, x*0.2, y*0.1);
    ui->deleteStuClass->setGeometry(x*0.4, y*0.6, x*0.2, y*0.1);

    ui->pushButtonExit->setGeometry(x*0.3, y*0.8, x*0.4, y*0.1);


    //QPalette pal = this->palette();
    //pal.setBrush(QPalette::Background,QBrush(QPixmap("background.jpg")));
    //setPalette(pal);

    QFont font("黑体",x*0.015);
    ui->label->setFont(font);
    ui->label->setGeometry(x*0.1, y*0.04,x*0.6, y*0.15);
    QFont fontBox("宋体", x*0.008);
    ui->studentsList->setFont(fontBox);
    ui->searchStu->setFont(fontBox);
    ui->searchClass->setFont(fontBox);
    ui->addStudent->setFont(fontBox);
    ui->deleteStudent->setFont(fontBox);
    ui->addStuClass->setFont(fontBox);
    ui->deleteStuClass->setFont(fontBox);
    ui->classesList->setFont(fontBox);
    ui->pushButtonExit->setFont(font);
}

void MainWindow::paintEvent(QPaintEvent *event) {
    QMainWindow::paintEvent(event); // 调用基类的 paintEvent

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

void MainWindow::on_classesList_clicked()
{
    ClassesListDialog classesList(this);
    classesList.exec();
}
