#include "student.h"

Student::Student(){ m_stuClassNum = 0; }
Student::~Student(){}

Student::Student(const Student& stu)
{
    *this = stu;
}

Student& Student::operator= (const Student& stu)
{
    m_stuID = stu.m_stuID;
    m_stuName = stu.m_stuName;
    m_stuCla = stu.m_stuCla;
    m_stuPhone = stu.m_stuPhone;
    m_stuBD = stu.m_stuBD;
    m_stuHome = stu.m_stuHome;
    m_stuClassNum = stu.m_stuClassNum;

    //初始化vector长度
    m_stuClasses.resize(m_stuClassNum);
    for (int i = 0; i<m_stuClassNum; i++)
        m_stuClasses[i] = stu.m_stuClasses[i];
    return *this;
}
