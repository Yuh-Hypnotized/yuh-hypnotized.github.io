#ifndef DELETESTUDENTCLASSESDIALOG_H
#define DELETESTUDENTCLASSESDIALOG_H

#include <QDialog>

namespace Ui {
class DeleteStudentClassesDialog;
}

class DeleteStudentClassesDialog : public QDialog
{
    Q_OBJECT

public:
    explicit DeleteStudentClassesDialog(QWidget *parent = nullptr);
    ~DeleteStudentClassesDialog();

private slots:
    void on_pushButton_2_clicked();

    void on_pushButton_clicked();

    void on_comboBox_currentTextChanged(const QString &arg1);

private:
    Ui::DeleteStudentClassesDialog *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // DELETESTUDENTCLASSESDIALOG_H
