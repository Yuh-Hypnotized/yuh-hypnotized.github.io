#ifndef STUDENTLISTDIALOG_H
#define STUDENTLISTDIALOG_H

#include <QWidget>
#include <QDialog>

namespace Ui {
class StudentListDialog;
}

class StudentListDialog : public QDialog
{
    Q_OBJECT

public:
    explicit StudentListDialog(QWidget *parent = nullptr);
    ~StudentListDialog();

private slots:
    void on_pushButton_clicked();

private:
    Ui::StudentListDialog *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;

public slots:
    void onItemClicked(int row, int column);

signals:
    void sendData(QString data);
};

#endif // STUDENTLISTDIALOG_H
