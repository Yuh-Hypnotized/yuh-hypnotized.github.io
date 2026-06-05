#include "addstudentclassesdialog.h"
#include "ui_addstudentclassesdialog.h"

#include <QFile>
#include <QDebug>
#include <QScreen>
#include <QPainter>
#include "faileddialog.h"
#include "successdialog.h"

AddStudentClassesDialog::AddStudentClassesDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::AddStudentClassesDialog)
{
    ui->setupUi(this);
    setWindowTitle("新增学生选课");

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

AddStudentClassesDialog::~AddStudentClassesDialog()
{
    delete ui;
}

//cancel
void AddStudentClassesDialog::on_pushButton_2_clicked()
{
    close();
}

//OK -> 检查学号是否有效 ->若有效则读取选课信息，若无效则弹出failed
void AddStudentClassesDialog::on_pushButton_clicked()
{
    QString input_stuID = ui->comboBox->currentText();

    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<<file.errorString();
    }
    else //打开文件
    {
        QTextStream in(&file);

        QVector<QString> linesVec; //linesVec用于储存原文件信息，并进行添加操作
        while(!in.atEnd())
        {
            QString line = in.readLine(); //读一行
            linesVec.push_back(line); //存进linesVec

            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

            QStringList studentItems = lineSplit1Vec[0].split(' ');
            QVector<QString> studentItemsVec = QVector<QString>::fromList(studentItems);

            if (studentItemsVec[0] == input_stuID) //找到 -> 进行加课操作
            {
                QString input_newClasses = ui->textEdit->toPlainText();
                if (input_newClasses.isEmpty()) //啥也没输入
                {
                    continue;
                }
                QStringList inputClassesSplit = input_newClasses.split('\n');
                QVector<QString> inputClassesVec = QVector<QString>::fromList(inputClassesSplit);
                //inputClassVec存储了新增的课程信息，一个元素为一整个课程信息

                for (int i=0; i<inputClassesVec.size(); i++)
                {
                    //按照\t分割的格式写入linesVec当前的最后一行
                    linesVec[linesVec.size()-1] += ('\t' + inputClassesVec[i]);
                }
            }
        }

        file.close();

        //以重写的方式写入文件
        file.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);

        QTextStream out(&file);
        for (auto line : linesVec)
        {
            out << line << '\n';
        }
        file.close();

        close();
        SuccessDialog success(this);
        success.exec();
    }
}


void AddStudentClassesDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label->setGeometry(x*0.1, y*0.1, x*0.8, y*0.05);
    ui->comboBox->setGeometry(x*0.1, y*0.15, x*0.8, y*0.1);
    ui->label_2->setGeometry(x*0.1, y*0.25, x*0.8, y*0.05);
    ui->label_3->setGeometry(x*0.1, y*0.3, x*0.8, y*0.05);
    ui->textEdit->setGeometry(x*0.1, y*0.35, x*0.8, y*0.4);
    ui->pushButton->setGeometry(x*0.2, y*0.8, x*0.2, y*0.1);
    ui->pushButton_2->setGeometry(x*0.6, y*0.8, x*0.2, y*0.1);

    QFont fontLabel("黑体", x*0.01);
    QFont fontBox("宋体", x*0.013);

    ui->label->setFont(fontLabel);
    ui->label_2->setFont(fontLabel);
    ui->label_3->setFont(fontLabel);
    ui->comboBox->setFont(fontBox);
    ui->pushButton->setFont(fontBox);
    ui->pushButton_2->setFont(fontBox);

}

void AddStudentClassesDialog::paintEvent(QPaintEvent *event) {
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

