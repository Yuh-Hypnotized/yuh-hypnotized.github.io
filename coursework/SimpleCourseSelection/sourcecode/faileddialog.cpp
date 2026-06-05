#include "faileddialog.h"
#include "ui_faileddialog.h"

FailedDialog::FailedDialog(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::FailedDialog)
{
    ui->setupUi(this);
    setWindowTitle("操作失败！");
}

FailedDialog::~FailedDialog()
{
    delete ui;
}

void FailedDialog::on_pushButton_clicked()
{
    close();
}
