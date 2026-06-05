#ifndef CLASSESDIALOG_H
#define CLASSESDIALOG_H

#include <QDialog>

namespace Ui {
class ClassesDialog;
}

class ClassesDialog : public QDialog
{
    Q_OBJECT

public:
    explicit ClassesDialog(QWidget *parent = nullptr);
    ~ClassesDialog();

private slots:
    void on_pushButton_clicked();

    void on_pushButton_2_clicked();

private:
    Ui::ClassesDialog *ui;

signals:
    void sendClassID(QString classID);

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // CLASSESDIALOG_H
