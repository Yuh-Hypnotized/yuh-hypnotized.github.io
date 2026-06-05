#include "classes.h"

Classes::Classes(){}
Classes::~Classes(){}


Classes::Classes(const Classes& cla)
{
    *this = cla;
}

Classes::Classes(QString id, QString name, float cre, int sem, QString room, float score)
    :m_classID(id), m_className(name), m_classCredit(cre),
      m_classSem(sem), m_classRoom(room), m_classScore(score){}

Classes& Classes::operator= (const Classes& cla)
{
    m_classID = cla.m_classID;
    m_className = cla.m_className;
    m_classCredit = cla.m_classCredit;
    m_classSem = cla.m_classSem;
    m_classRoom = cla.m_classRoom;
    m_classScore = cla.m_classScore;
    return *this;
}

void Classes::SaveClass(QTextStream& aStream)
{
    aStream<<m_classID<<" "<<m_className<<" "<<m_classCredit<<" "
          <<m_classSem<<" "<<m_classRoom<<" "<<m_classScore<<"\n";
}

void Classes::ReadClass(QTextStream& aStream)
{
    aStream>>m_classID>>m_className>>m_classCredit>>m_classSem
            >>m_classRoom>>m_classScore;
}
