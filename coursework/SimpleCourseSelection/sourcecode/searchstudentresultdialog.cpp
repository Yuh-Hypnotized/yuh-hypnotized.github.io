#include "searchstudentresultdialog.h"
#include "ui_searchstudentresultdialog.h"

#include <QScreen>
#include <QPainter>

SearchStudentResultDialog::SearchStudentResultDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::SearchStudentResultDialog)
{
    ui->setupUi(this);
    setWindowTitle("查询学生结果");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.4, screenHeight * 0.6);
}

SearchStudentResultDialog::~SearchStudentResultDialog()
{
    delete ui;
}

//退出Exit
void SearchStudentResultDialog::on_pushButton_clicked()
{
    close();
}

//slot
void SearchStudentResultDialog::receiveData(QString data)
{
    QStringList lineSplit1 = data.split('\t');
    QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

    //第一部分：个人信息
    QStringList studentItems = lineSplit1Vec[0].split(' ');
    QVector<QString> studentItemsVec = QVector<QString>::fromList(studentItems);
    ui->label_stuID->setText(studentItemsVec[0]);
    ui->label_stuName->setText(studentItemsVec[1]);
    ui->label_stuCla->setText(studentItemsVec[2]);
    ui->label_stuPhone->setText(studentItemsVec[3]);
    ui->label_stuBD->setText(studentItemsVec[4]);
    ui->label_stuHome->setText(studentItemsVec[5]);

    //第二部分：选课信息

    ui->tableWidget->setColumnCount(6); //课程编号 课程名称 学分 学期 教室 成绩
    QStringList headerText;
    headerText<<"课程编号"<<"课程名称"<<"学分"<<"学期"<<"教室"<<"成绩";
    ui->tableWidget->setHorizontalHeaderLabels(headerText);
    //设置列宽


    ui->tableWidget->setEditTriggers(QAbstractItemView::NoEditTriggers);//不允许编辑
    ui->tableWidget->setRowCount(lineSplit1Vec.size()-1);//设置行数

    //写入选课信息
    for (int row=1; row<lineSplit1Vec.size(); row++)
    {
        QStringList classData = lineSplit1Vec[row].split(' ');
        QVector<QString> classDataVec = QVector<QString>::fromList(classData);
        for (int col=0; col<6; col++)
        {
            ui->tableWidget->setItem(row-1,col, new QTableWidgetItem(classDataVec[col]));
        }
    }
}


void SearchStudentResultDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    QFont headerFont("黑体", x*0.015);
    QFont leftFont("黑体", x*0.01);
    QFont rightFont("宋体", x*0.01);

    //调整控件位置和大小
    ui->label->setGeometry(x*0.1, y*0.03, x*0.6, y*0.1);

    ui->label_2->setGeometry(x*0.1, y*0.15, x*0.3, y*0.05);
    ui->label_stuID->setGeometry(x*0.4, y*0.15, x*0.6, y*0.05);

    ui->label_3->setGeometry(x*0.1, y*0.2, x*0.3, y*0.05);
    ui->label_stuName->setGeometry(x*0.4, y*0.2, x*0.6, y*0.05);

    ui->label_4->setGeometry(x*0.1, y*0.25, x*0.3, y*0.05);
    ui->label_stuCla->setGeometry(x*0.4, y*0.25, x*0.6, y*0.05);

    ui->label_5->setGeometry(x*0.1, y*0.3, x*0.3, y*0.05);
    ui->label_stuPhone->setGeometry(x*0.4, y*0.3, x*0.6, y*0.05);

    ui->label_7->setGeometry(x*0.1, y*0.35, x*0.3, y*0.05);
    ui->label_stuBD->setGeometry(x*0.4, y*0.35, x*0.6, y*0.05);

    ui->label_9->setGeometry(x*0.1, y*0.4, x*0.3, y*0.05);
    ui->label_stuHome->setGeometry(x*0.4, y*0.4, x*0.6, y*0.05);

    ui->label_6->setGeometry(x*0.1, y*0.45, x*0.3, y*0.05);
    ui->tableWidget->setGeometry(x*0.1, y*0.5, x*0.8, y*0.3);

    ui->pushButton->setGeometry(x*0.3, y*0.85, x*0.4, y*0.05);

    //调整字体
    ui->label->setFont(headerFont);
    ui->label_2->setFont(leftFont);
    ui->label_3->setFont(leftFont);
    ui->label_4->setFont(leftFont);
    ui->label_5->setFont(leftFont);
    ui->label_7->setFont(leftFont);
    ui->label_9->setFont(leftFont);
    ui->label_6->setFont(leftFont);
    ui->label_stuID->setFont(rightFont);
    ui->label_stuName->setFont(rightFont);
    ui->label_stuCla->setFont(rightFont);
    ui->label_stuPhone->setFont(rightFont);
    ui->label_stuBD->setFont(rightFont);
    ui->label_stuHome->setFont(rightFont);

    //调整表格列宽
    ui->tableWidget->setColumnWidth(0,x*0.2);
    ui->tableWidget->setColumnWidth(1,x*0.3);
    ui->tableWidget->setColumnWidth(2,x*0.05);
    ui->tableWidget->setColumnWidth(3,x*0.05);
    ui->tableWidget->setColumnWidth(4,x*0.1);
    ui->tableWidget->setColumnWidth(5,x*0.07);
}


void SearchStudentResultDialog::paintEvent(QPaintEvent *event) {
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
