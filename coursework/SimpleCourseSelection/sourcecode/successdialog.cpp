#include "successdialog.h"
#include "ui_successdialog.h"

SuccessDialog::SuccessDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::SuccessDialog)
{
    ui->setupUi(this);
    setWindowTitle("操作成功！");
}

SuccessDialog::~SuccessDialog()
{
    delete ui;
}

//点下OK 弹窗关闭
void SuccessDialog::on_pushButton_clicked()
{
    close();
}
