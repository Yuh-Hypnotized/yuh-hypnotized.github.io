#ifndef CLASSESLISTDIALOG_H
#define CLASSESLISTDIALOG_H

#include <QDialog>

namespace Ui {
class ClassesListDialog;
}

class ClassesListDialog : public QDialog
{
    Q_OBJECT

public:
    explicit ClassesListDialog(QWidget *parent = nullptr);
    ~ClassesListDialog();

private:
    Ui::ClassesListDialog *ui;

public slots:
    void onItemClicked(int row, int col);

signals:
    void sendData(QString classID);

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
private slots:
    void on_pushButton_clicked();
};

#endif // CLASSESLISTDIALOG_H
