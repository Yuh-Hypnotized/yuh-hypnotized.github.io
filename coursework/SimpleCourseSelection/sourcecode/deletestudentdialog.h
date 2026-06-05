#ifndef DELETESTUDENTDIALOG_H
#define DELETESTUDENTDIALOG_H

#include <QWidget>
#include <QDialog>

namespace Ui {
class DeleteStudentDialog;
}

class DeleteStudentDialog : public QDialog
{
    Q_OBJECT

public:
    explicit DeleteStudentDialog(QWidget *parent = nullptr);
    ~DeleteStudentDialog();

private slots:
    void on_pushButton_clicked();

    void on_pushButtonCancel_clicked();

private:
    Ui::DeleteStudentDialog *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // DELETESTUDENTDIALOG_H
