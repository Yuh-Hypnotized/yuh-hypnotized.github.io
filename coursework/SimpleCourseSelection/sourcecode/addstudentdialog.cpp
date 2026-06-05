#include "addstudentdialog.h"
#include "ui_addstudentdialog.h"
#include <QVector>
#include <QFile>
#include <QDebug>
#include <QDir>
#include <QScreen>
#include <QPainter>
#include "successdialog.h"

AddStudentDialog::AddStudentDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::AddStudentDialog)
{
    ui->setupUi(this);
    setWindowTitle("新增学生");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.4, screenHeight * 0.6);
}

AddStudentDialog::~AddStudentDialog()
{
    delete ui;
}

//Cancel键 clicked 关闭窗口
void AddStudentDialog::on_pushButton_2_clicked()
{
    close();
}


//OK键 clicked 将新学生信息存入文件
void AddStudentDialog::on_pushButton_clicked()
{
    //t_classes用来存储该学生的选课
    QVector<Classes> t_classes;
    QString plainText = ui->textEditClasses->toPlainText();
    QStringList lines = plainText.split('\n'); //分行
    for (auto line : lines)
    {
        QStringList items = line.split(' '); //一行中以空格分割
        QVector <QString> itemsVec = QVector <QString>::fromList(items);
        //转换格式，并存入t_classes
        t_classes.push_back(Classes(itemsVec[0], itemsVec[1], itemsVec[2].toFloat(),
                itemsVec[3].toInt(), itemsVec[4], itemsVec[5].toFloat()));
    }

    //写入文件
    QFile file("Students.txt");
    if (!file.open(QIODevice::WriteOnly | QIODevice::Append | QIODevice::Text))
    {
        qWarning()<<"Cannot open file for writing: "<<file.errorString();
    }
    else
    {

        QTextStream out(&file);
        //个人信息
        out<<ui->lineEditID->text()<<' '<<ui->lineEditName->text()<<' '<<ui->lineEditCla->text()<<' '
          <<ui->lineEditPhone->text()<<' '<<ui->dateEdit->text()<<' '<<ui->lineEditHome->text();
        //选课信息（\t分割）
        for (auto classes : t_classes)
        {
            out<<'\t'<<classes.m_classID<<' '<<classes.m_className<<' '<<classes.m_classCredit
              <<' '<<classes.m_classSem<<' '<<classes.m_classRoom<<' '<<classes.m_classScore;
        }
        out << '\n'; //换行收尾
        file.close();
    }
    close();
    SuccessDialog success(this);
    success.exec();
}

void AddStudentDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    ui->label_2->setGeometry(x*0.1, y*0.1, x*0.2, y*0.05);
    ui->lineEditID->setGeometry(x*0.35, y*0.105, x*0.55, y*0.04);

    ui->label_3->setGeometry(x*0.1, y*0.15, x*0.2, y*0.05);
    ui->lineEditName->setGeometry(x*0.35, y*0.155, x*0.55, y*0.04);

    ui->label_4->setGeometry(x*0.1, y*0.2, x*0.2, y*0.05);
    ui->lineEditCla->setGeometry(x*0.35, y*0.205, x*0.55, y*0.04);

    ui->label_5->setGeometry(x*0.1, y*0.25, x*0.2, y*0.05);
    ui->lineEditPhone->setGeometry(x*0.35, y*0.255, x*0.55, y*0.04);

    ui->label_6->setGeometry(x*0.1, y*0.3, x*0.2, y*0.05);
    ui->dateEdit->setGeometry(x*0.35, y*0.305, x*0.55, y*0.04);

    ui->label_7->setGeometry(x*0.1, y*0.35, x*0.2, y*0.05);
    ui->lineEditHome->setGeometry(x*0.35, y*0.355, x*0.55, y*0.04);

    ui->label->setGeometry(x*0.1, y*0.4, x*0.2, y*0.05);
    ui->textEditClasses->setGeometry(x*0.1, y*0.45, x*0.8, y*0.35);

    ui->label_8->setGeometry(x*0.1, y*0.8, x*0.8, y*0.05);

    ui->pushButton->setGeometry(x*0.2, y*0.85, x*0.2, y*0.05);
    ui->pushButton_2->setGeometry(x*0.6, y*0.85, x*0.2, y*0.05);

    QFont font("黑体", x*0.01);
    ui->label_2->setFont(font);
    ui->label_3->setFont(font);
    ui->label_4->setFont(font);
    ui->label_5->setFont(font);
    ui->label_6->setFont(font);
    ui->label_7->setFont(font);
    ui->label->setFont(font);
    ui->label_8->setFont(font);

}


void AddStudentDialog::paintEvent(QPaintEvent *event) {
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
