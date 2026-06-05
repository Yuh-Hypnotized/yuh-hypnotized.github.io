#ifndef STUDENT_H
#define STUDENT_H

#include "classes.h"
#include <QVector>
#include <QDate>

class Student
{
public:
    Student();
    virtual ~Student();

    Student(const Student& stu);
    Student& operator= (const Student& stu);

    //学号，姓名，班级，电话，生日，住址，选课数据集
    QString m_stuID;
    QString m_stuName;
    QString m_stuCla;
    QString m_stuPhone;
    QDate m_stuBD;
    QString m_stuHome;
    QVector<Classes> m_stuClasses;
    int m_stuClassNum; //选课数量
};

#endif // STUDENT_H
