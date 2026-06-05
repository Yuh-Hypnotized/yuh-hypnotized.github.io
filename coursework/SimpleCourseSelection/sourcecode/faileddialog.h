#ifndef FAILEDDIALOG_H
#define FAILEDDIALOG_H

#include <QWidget>
#include <QDialog>

namespace Ui {
class FailedDialog;
}

class FailedDialog : public QDialog
{
    Q_OBJECT

public:
    explicit FailedDialog(QWidget *parent = nullptr);
    ~FailedDialog();

private slots:
    void on_pushButton_clicked();

private:
    Ui::FailedDialog *ui;
};

#endif // FAILEDDIALOG_H
