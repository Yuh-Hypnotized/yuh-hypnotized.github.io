#include "classeslistdialog.h"
#include "ui_classeslistdialog.h"

#include <QScreen>
#include <QFile>
#include <QTextStream>
#include <QTableWidget>
#include <QPainter>
#include "displayclassstudentsdialog.h"

ClassesListDialog::ClassesListDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::ClassesListDialog)
{
    ui->setupUi(this);
    setWindowTitle("课程信息列表");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.6, screenHeight * 0.6);

    //从文件中读取课程信息
    QVector< QVector<QString> > classesInfoVector;

    QFile file("Students.txt");
    file.open(QIODevice::ReadOnly | QIODevice::Text);

    QTextStream in(&file);
    while (!in.atEnd())
    {
        QString line = in.readLine();
        QStringList lineSplit1 = line.split('\t');
        QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

        for (int i=1; i<lineSplit1Vec.size(); i++)
        {
            QStringList classInfo = lineSplit1Vec[i].split(' ');
            QVector<QString> classInfoVec = QVector<QString>::fromList(classInfo);
            classInfoVec.pop_back(); //去掉成绩
            //如果该课程还没有记录 则记录
            if (!classesInfoVector.contains(classInfoVec))
            {
                classesInfoVector.push_back(classInfoVec);
            }
        }
    }
    file.close();

    //设置表格内容
    ui->tableWidget->setColumnCount(6);
    QStringList headerText;
    headerText <<"课程编号"<<"课程名称"<<"学分"<<"学期"<<"教室"<<" ";
    ui->tableWidget->setHorizontalHeaderLabels(headerText);

    ui->tableWidget->setRowCount(classesInfoVector.size());
    for (int i=0; i<classesInfoVector.size(); i++)
    {
        for (int j=0; j<5; j++)
        {
            ui->tableWidget->setItem(i,j,new QTableWidgetItem(classesInfoVector[i][j]));
        }
        ui->tableWidget->setItem(i,5,new QTableWidgetItem("查看"));
    }

    connect(ui->tableWidget, &QTableWidget::cellClicked, this, &ClassesListDialog::onItemClicked);
}

ClassesListDialog::~ClassesListDialog()
{
    delete ui;
}

//点击"查看"时 弹出该课程的具体信息
void ClassesListDialog::onItemClicked(int row, int column)
{
    if (column == 5)
    {
        DisplayClassStudentsDialog displayClassDialog(this);
        connect(this, &ClassesListDialog::sendData, &displayClassDialog, &DisplayClassStudentsDialog::receiveClassID);
        emit(sendData(ui->tableWidget->item(row,0)->text()));
        displayClassDialog.exec();
    }
}


void ClassesListDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->tableWidget->setGeometry(x*0.02, y*0.01, x*0.96, y*0.74);
    ui->pushButton->setGeometry(x*0.3, y*0.8, x*0.4, y*0.1);

    ui->tableWidget->setColumnWidth(0,x*0.25); //课程编号
    ui->tableWidget->setColumnWidth(1,x*0.25); //课程名称
    ui->tableWidget->setColumnWidth(2,x*0.1); //学分
    ui->tableWidget->setColumnWidth(3,x*0.1); //学期
    ui->tableWidget->setColumnWidth(4,x*0.13); //教室
    ui->tableWidget->setColumnWidth(5,x*0.1); //查看
}

void ClassesListDialog::paintEvent(QPaintEvent *event) {
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

void ClassesListDialog::on_pushButton_clicked()
{
    close();
}
