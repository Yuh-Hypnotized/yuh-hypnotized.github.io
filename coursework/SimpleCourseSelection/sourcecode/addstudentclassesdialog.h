#ifndef ADDSTUDENTCLASSESDIALOG_H
#define ADDSTUDENTCLASSESDIALOG_H

#include <QDialog>

namespace Ui {
class AddStudentClassesDialog;
}

class AddStudentClassesDialog : public QDialog
{
    Q_OBJECT

public:
    explicit AddStudentClassesDialog(QWidget *parent = nullptr);
    ~AddStudentClassesDialog();

private slots:
    void on_pushButton_2_clicked();

    void on_pushButton_clicked();

private:
    Ui::AddStudentClassesDialog *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // ADDSTUDENTCLASSESDIALOG_H
