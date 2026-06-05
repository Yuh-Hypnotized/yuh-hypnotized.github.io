#ifndef ADDSTUDENTDIALOG_H
#define ADDSTUDENTDIALOG_H

#include <QWidget>
#include <QDialog>
#include "student.h"

namespace Ui {
class AddStudentDialog;
}

class AddStudentDialog : public QDialog
{
    Q_OBJECT

public:
    explicit AddStudentDialog(QWidget *parent = nullptr);
    ~AddStudentDialog();

private slots:
    void on_pushButton_2_clicked();  //cancel

    void on_pushButton_clicked(); //OK!

private:
    Ui::AddStudentDialog *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // ADDSTUDENTDIALOG_H
