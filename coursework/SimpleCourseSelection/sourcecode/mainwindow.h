#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "addstudentdialog.h"

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void on_addStudent_clicked();

    void on_pushButtonExit_clicked();

    void on_studentsList_clicked();

    void on_deleteStudent_clicked();

    void on_searchStu_clicked();

    void on_addStuClass_clicked();

    void on_deleteStuClass_clicked();

    void on_searchClass_clicked();

    void on_classesList_clicked();

private:
    Ui::MainWindow *ui;

protected:
    virtual void resizeEvent(QResizeEvent *event) override;
    virtual void paintEvent(QPaintEvent *event) override;
};
#endif // MAINWINDOW_H
