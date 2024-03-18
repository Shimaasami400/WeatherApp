//stack
#include <iostream>
#include <string>
using namespace std;


class Employees {
public:
    int code ;
    int salary ;
    string name ;
public:
    Employees *pNext;
    Employees *pPrevious;
    Employees() {
        code = 0;
        name = "NO Name";
        salary = 0;
        pNext = NULL;
        pPrevious = NULL;
}

    void setCode(int vcode){
        vcode = code ;
    }
    void setSalary(int vSalary){
        vSalary = salary ;
    }
    int getCode()
    {
        return code ;
    }
    int getSalary(){
        return salary ;
    }

    void setName(string vname){
        vname = name;
    }
    string getName()
    {
        return name;
    }
};

class LinkedList {
public:
    Employees *pStart;
    Employees *pEnd;

public:
    LinkedList(){
        pStart=pEnd=NULL;
        }
    ~LinkedList() {
        freeList();
         }
    void addList(Employees *pItem);
    void InsertList(Employees *pItem);
    Employees* SearchList(int Code);
    int DeleteList(int Code);
    void freeList();
    void displayAll();
};


class Stack : private LinkedList{

public:
void push (Employees *e){
    addList(e);
}

Employees* pop(){
    Employees *pNode;
    pNode = pEnd;
    return pNode;
}
int deleteStack(int Code){
    return DeleteList(Code);
}
Employees* searchStack(int Code){

    return SearchList(Code);
}

void freeStack(){
    freeList();
}
void displayAll(){
    LinkedList:: displayAll();
}

};
int main()
{

    int n;
    cout << "Enter number of Employees : " ;
    cin >> n ;
    Stack s ;
     Employees *a = new Employees[n];
    for (int j = 0 ; j < n ; j ++ ){

        s.push(&a[j]);
         cout << "Name of employee number "<< j+1 << " :" ;
        cin >> a[j].name ;
        cout << "Code "<< j + 1 << " :" ;
        cin >> a[j].code ;
       cout << "Salary "<< j +1<< " :" ;
        cin >>  a[j].salary ;
        cout << endl ;

    }

    cout <<endl ;
    s.displayAll();


    // search
    int searchCode;
    cout << "Enter the code of the employee you want to search for: ";
    cin >> searchCode;

    Employees* found = s.searchStack(searchCode);

    if (found) {
        cout << "Employee found!" << endl;
        cout << "Code: " << found->getCode() << endl;
        cout << "Name: " << found->getName() << endl;
        cout << "Salary: " << found->getSalary() << endl;
    } else {
        cout << "Employee not found." << endl;
    }

    cout << "Enter Code you want to delete : \t" ;
    int deleteCode  ;
    cin >> deleteCode ;
    int retFlag = s.deleteStack(deleteCode);
    if (retFlag == 0) {
        cout << "Employee not found." << endl;
    } else {
        cout << "Employee deleted successfully." << endl;
    }

    s.displayAll();


    return 0;
}




void LinkedList :: addList(Employees *pItem){
    if (!pStart) {
        pItem->pNext = NULL;
        pItem->pPrevious = NULL;
        pStart = pEnd = pItem;
    } else {
        pEnd->pNext = pItem;
        pItem->pPrevious = pEnd;
        pItem->pNext = NULL;
        pEnd = pItem;
    }
}
Employees* LinkedList :: SearchList(int key)
{
    Employees* pItem = pStart;
    while (pItem && pItem->getCode() != key) {
        pItem = pItem->pNext;
    }
    return pItem;
}

void LinkedList::displayAll() {

    Employees* pItem = pStart;
    while (pItem) {
        cout << "Code: " << pItem->getCode() << endl;
        cout << "Name: " << pItem->getName() << endl;
        cout << "Salary: " << pItem->getSalary() << endl;
        cout << "---------------------------" << endl;

        pItem = pItem->pNext;
    }
}

void LinkedList :: InsertList(Employees *pNode){
    Employees *ptemp;
    if(pStart==NULL) {
        addList (pNode);
    }
    else{
        ptemp=pStart;
        while(ptemp && ptemp ->getCode() < pNode->getCode() )
            {
                ptemp= ptemp->pNext;
            }
        if(!ptemp) {
            addList (pNode);
        }
        else if(ptemp==pStart) {
             pNode->pNext=pStart;
             pNode->pPrevious=NULL;
             pStart->pPrevious=pNode;
             pStart=pNode;
        }
        else{
            pNode->pNext=ptemp;
            pNode->pPrevious=ptemp->pPrevious;
            ptemp->pPrevious->pNext=pNode;
            ptemp->pPrevious=pNode;
        }
    }
}

void LinkedList :: freeList(){
    Employees * pItem;
    while(pStart)
    {
        pItem = pStart;
        pStart = pStart->pNext;
        delete pItem;
    }
    pEnd = NULL;
}

int LinkedList :: DeleteList(int Code){
    Employees *pItem;
    int RetFlag = 1;
    pItem = SearchList(Code);
    if (!pItem){
        RetFlag = 0;
    }
    else{
        if (pStart == pEnd){
            pStart = pEnd = NULL;
        }
        else if(pItem == pStart){
            pStart = pStart->pNext;
            pStart->pPrevious = NULL;
        }
        else if(pItem == pEnd){
            pEnd = pEnd->pPrevious;
            pEnd->pNext = NULL;
        }
        else{
            pItem->pPrevious->pNext = pItem->pNext;
            pItem->pNext->pPrevious = pItem->pPrevious;
        }
        //delete pItem;
    }
    return RetFlag;
}
