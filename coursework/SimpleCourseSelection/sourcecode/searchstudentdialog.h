#ifndef SEARCHSTUDENTDIALOG_H
#define SEARCHSTUDENTDIALOG_H

#include <QWidget>
#include <QDialog>
#include <QLineEdit>

namespace Ui {
class SearchStudentDialog;
}

class SearchStudentDialog : public QDialog
{
    Q_OBJECT

public:
    explicit SearchStudentDialog(QWidget *parent = nullptr);
    ~SearchStudentDialog();

private slots:
    void on_pushButton_clicked();

    void on_pushButton_2_clicked();

private:
    Ui::SearchStudentDialog *ui;

signals:
    void sendData(QString data);

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // SEARCHSTUDENTDIALOG_H
