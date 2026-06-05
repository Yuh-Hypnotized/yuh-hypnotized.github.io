#ifndef DISPLAYCLASSSTUDENTSDIALOG_H
#define DISPLAYCLASSSTUDENTSDIALOG_H

#include <QDialog>

namespace Ui {
class DisplayClassStudentsDialog;
}

class DisplayClassStudentsDialog : public QDialog
{
    Q_OBJECT

public:
    explicit DisplayClassStudentsDialog(QWidget *parent = nullptr);
    ~DisplayClassStudentsDialog();

private slots:
    void on_pushButton_clicked();

private:
    Ui::DisplayClassStudentsDialog *ui;

public slots:
    void receiveClassID(QString classID);

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};

#endif // DISPLAYCLASSSTUDENTSDIALOG_H
