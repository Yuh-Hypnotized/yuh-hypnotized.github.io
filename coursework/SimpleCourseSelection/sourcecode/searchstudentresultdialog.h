#ifndef SEARCHSTUDENTRESULTDIALOG_H
#define SEARCHSTUDENTRESULTDIALOG_H

#include <QWidget>
#include <QDialog>

namespace Ui {
class SearchStudentResultDialog;
}

class SearchStudentResultDialog : public QDialog
{
    Q_OBJECT

public:
    explicit SearchStudentResultDialog(QWidget *parent = nullptr);
    ~SearchStudentResultDialog();

private slots:
    void on_pushButton_clicked();

private:
    Ui::SearchStudentResultDialog *ui;

public slots:
    void receiveData(QString data); //接收学生数据

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // SEARCHSTUDENTRESULTDIALOG_H
