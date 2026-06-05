#include "classesdialog.h"
#include "ui_classesdialog.h"

#include "displayclassstudentsdialog.h"
#include <QFile>
#include <QDebug>
#include <QScreen>
#include <QPainter>

ClassesDialog::ClassesDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::ClassesDialog)
{
    ui->setupUi(this);
    setWindowTitle("查询课程");

    QRect screenGeometry = QGuiApplication::primaryScreen()->geometry();
    int screenWidth = screenGeometry.width();
    int screenHeight = screenGeometry.height();

    this->resize(screenWidth * 0.4, screenHeight * 0.6);

    //在combobox中显示所有课程的选项
    //先统计所有存在的课程
    QVector<QString> classesVector;


    QFile file("Students.txt");
    if (!file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        qWarning() << "Cannot open file for reading: "<<file.errorString();
    }
    else
    {
        QTextStream in(&file);
        while (!in.atEnd())
        {
            QString line = in.readLine();
            QStringList lineSplit1 = line.split('\t');
            QVector<QString> lineSplit1Vec = QVector<QString>::fromList(lineSplit1);

            for (int i=1; i<lineSplit1Vec.size(); i++)
            {
                QStringList classItems = lineSplit1Vec[i].split(' ');
                QVector<QString> classItemsVec = QVector<QString>::fromList(classItems);
                if (!classesVector.contains(classItemsVec[0]))
                {
                    classesVector.push_back(classItemsVec[0]);
                }
            }
        }
    }

    for (int i=0; i<classesVector.size(); i++)
    {
        ui->comboBox->addItem(classesVector[i]);
    }
}

ClassesDialog::~ClassesDialog()
{
    delete ui;
}

//OK !
void ClassesDialog::on_pushButton_clicked()
{
    //信号与槽，发送combobox中的课程信息
    DisplayClassStudentsDialog displayDialog(this);
    connect(this, &ClassesDialog::sendClassID, &displayDialog, &DisplayClassStudentsDialog::receiveClassID);
    emit sendClassID(ui->comboBox->currentText());
    close();
    displayDialog.exec();
}

//Cancel
void ClassesDialog::on_pushButton_2_clicked()
{
    close();
}


void ClassesDialog::resizeEvent(QResizeEvent *event)
{
    QDialog::resizeEvent(event);

    int x = this->frameGeometry().width();
    int y = this->frameGeometry().height();

    QFont font("黑体",x*0.02);
    ui->label->setFont(font);
    ui->label->setGeometry(x*0.1, y*0.2, x*0.8, y*0.1);
    ui->comboBox->setGeometry(x*0.1, y*0.4, x*0.8, y*0.1);
    ui->pushButton->setGeometry(x*0.2, y*0.7, x*0.2, y*0.1);
    ui->pushButton_2->setGeometry(x*0.6, y*0.7, x*0.2, y*0.1);

    QFont fontBox("宋体", x*0.013);
    ui->comboBox->setFont(fontBox);
    ui->pushButton->setFont(fontBox);
    ui->pushButton_2->setFont(fontBox);

}


void ClassesDialog::paintEvent(QPaintEvent *event) {
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
