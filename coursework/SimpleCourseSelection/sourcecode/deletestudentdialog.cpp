#include "deletestudentdialog.h"
#include "ui_deletestudentdialog.h"

#include <QString>
#include <QFile>
#include <QDebug>
#include <QScreen>
#include <QPainter>
#include "successdialog.h"
#include "faileddialog.h"

DeleteStudentDialog::DeleteStudentDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::DeleteStudentDialog)
{
    ui->setupUi(this);
    setWindowTitle("删除学生");

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

DeleteStudentDialog::~DeleteStudentDialog()
{
    delete ui;
}


//按下OK键 -> 检查数据是否有效
//->若有效则删除学生，进入successDialog ； 若无效则进入failedDialog
void DeleteStudentDialog::on_pushButton_clicked()
{
    QString input_stuID = ui->comboBox->currentText(); //选择的学号

    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<< file.errorString();
    }
    else
    {
        QTextStream in(&file);

        QVector<QString> linesVector; //用于存储每一行的信息
        while(!in.atEnd())
        {
            QString line = in.readLine();
            linesVector.push_back(line); //存一行
        }
        file.close();

        //遍历每一行，找到要删除的学生
        for (int i=0; i<linesVector.size(); i++)
        {
            QString t_line = linesVector[i];
            QStringList lineSplit1 = t_line.split('\t'); //分离出个人信息部分
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);
            QStringList studentItems = lineSplit1Vec[0].split(' '); //分离出学号
            QVector<QString> studentItemsVec = QVector<QString>::fromList(studentItems);

            if (studentItemsVec[0] == input_stuID) //符合条件
            {
                linesVector.removeAt(i); //删除这一行
                break; //退出for循环
            }
        }

        //以重写方式打开文件，重新写入数据
        file.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Truncate);
        QTextStream out(&file);
        for (auto line : linesVector)
        {
            out << line << '\n';
        }
        file.close();
        //关闭当前窗口 弹出操作成功窗口
        close();
        SuccessDialog success(this);
        success.exec();
    }
}

//关闭窗口
void DeleteStudentDialog::on_pushButtonCancel_clicked()
{
    close();
}


void DeleteStudentDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label->setGeometry(x*0.1, y*0.2, x*0.8, y*0.1);
    ui->comboBox->setGeometry(x*0.1, y*0.4, x*0.8, y*0.1);
    ui->pushButton->setGeometry(x*0.2, y*0.8, x*0.2, y*0.1);
    ui->pushButtonCancel->setGeometry(x*0.6, y*0.8, x*0.2, y*0.1);

    QFont fontLabel("黑体", x*0.02);
    QFont fontBox("宋体", x*0.013);
    ui->label->setFont(fontLabel);
    ui->comboBox->setFont(fontBox);
    ui->pushButton->setFont(fontBox);
    ui->pushButtonCancel->setFont(fontBox);
}


void DeleteStudentDialog::paintEvent(QPaintEvent *event) {
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
