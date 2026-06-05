#ifndef CLASSES_H
#define CLASSES_H

#include <QString>
#include <QTextStream>

class Classes
{
public:
    Classes();
    virtual ~Classes();

    Classes(const Classes& cla);
    Classes(QString id, QString name, float cre, int sem, QString room, float score);
    Classes& operator= (const Classes& cla);

    void SaveClass(QTextStream& aStream);
    void ReadClass(QTextStream& aStream);



    //课程编号，课程名称，学分，学期，教室，成绩
    QString m_classID;
    QString m_className;
    float m_classCredit;
    int m_classSem;
    QString m_classRoom;
    float m_classScore;
};

#endif // CLASSES_H
