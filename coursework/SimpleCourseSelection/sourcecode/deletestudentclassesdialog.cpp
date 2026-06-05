#include "deletestudentclassesdialog.h"
#include "ui_deletestudentclassesdialog.h"

#include <QFile>
#include <QDebug>
#include <QScreen>
#include <QPainter>
#include "successdialog.h"
#include "faileddialog.h"

DeleteStudentClassesDialog::DeleteStudentClassesDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::DeleteStudentClassesDialog)
{
    ui->setupUi(this);
    setWindowTitle("删除学生选课");

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

DeleteStudentClassesDialog::~DeleteStudentClassesDialog()
{
    delete ui;
}

//cancel
void DeleteStudentClassesDialog::on_pushButton_2_clicked()
{
    close();
}

//OK -> 检查学号是否有效 -> 进行操作
void DeleteStudentClassesDialog::on_pushButton_clicked()
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

        QVector<QString> linesVec; //linesVec用于储存原文件信息，并进行删除操作
        while(!in.atEnd())
        {
            QString line = in.readLine(); //读一行
            linesVec.push_back(line); //存进linesVec

            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

            QStringList studentItems = lineSplit1Vec[0].split(' ');
            QVector<QString> studentItemsVec = QVector<QString>::fromList(studentItems);

            if (studentItemsVec[0] == input_stuID) //找到学生 -> 进行删课操作
            {
                QString inputClasses = ui->comboBox_2->currentText(); //要删除的课程id

                for (int j=1; j<lineSplit1Vec.size(); j++) //遍历每个课程 找到所需删除的课
                {
                    QStringList classItems = lineSplit1Vec[j].split(' ');
                    QVector<QString> classItemsVec = QVector<QString>::fromList(classItems);

                    if (classItemsVec[0] == inputClasses)
                    {
                        lineSplit1Vec.removeAt(j);
                        break; //退出内层for循环
                    }
                }
                //重新整理这一行的内容
                linesVec[linesVec.size()-1] = lineSplit1Vec[0];
                for (int i=1; i<lineSplit1Vec.size(); i++)
                {
                    linesVec[linesVec.size()-1] += ('\t' + lineSplit1Vec[i]);
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

//用户选择学号后 改变
void DeleteStudentClassesDialog::on_comboBox_currentTextChanged(const QString &arg1)
{
    ui->comboBox_2->clear();

    QFile file("Students.txt");
    file.open(QIODevice::ReadOnly | QIODevice::Text);
    QTextStream in(&file);

    QVector<QString> classIDs; //储存所有学生的学号
    while (!in.atEnd())
    {
        QString line = in.readLine();
        QStringList lineSplit1 = line.split('\t');
        QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

        QStringList stuItems = lineSplit1Vec[0].split(' ');
        QVector<QString> stuItemsVec = QVector<QString>::fromList(stuItems);

        if (arg1 == stuItemsVec[0])
        {
            for (int i=1; i<lineSplit1Vec.size(); i++) //将课程id录入第二个combobox中
            {
                QStringList classItems = lineSplit1Vec[i].split(' ');
                QVector<QString> classItemsVec = QVector<QString>::fromList(classItems);
                classIDs.push_back(classItemsVec[0]);
            }
            break;
        }
    }
    file.close();

    for (auto id : classIDs)
    {
        ui->comboBox_2->addItem(id);
    }
}


void DeleteStudentClassesDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label->setGeometry(x*0.1, y*0.1, x*0.8, y*0.1);
    ui->comboBox->setGeometry(x*0.1, y*0.25, x*0.8, y*0.1);
    ui->label_2->setGeometry(x*0.1, y*0.4, x*0.8, y*0.1);
    ui->comboBox_2->setGeometry(x*0.1, y*0.55, x*0.8, y*0.1);
    ui->pushButton->setGeometry(x*0.2, y*0.8, x*0.2, y*0.1);
    ui->pushButton_2->setGeometry(x*0.6, y*0.8, x*0.2, y*0.1);

    QFont fontLabel("黑体", x*0.02);
    QFont fontBox("宋体", x*0.013);

    ui->label->setFont(fontLabel);
    ui->label_2->setFont(fontLabel);
    ui->comboBox->setFont(fontBox);
    ui->comboBox_2->setFont(fontBox);
    ui->pushButton->setFont(fontBox);
    ui->pushButton_2->setFont(fontBox);
}


void DeleteStudentClassesDialog::paintEvent(QPaintEvent *event) {
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
