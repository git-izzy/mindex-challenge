# Task 1

Create a new type called `ReportingStructure` that has two fields: `employee` and `numberOfReports`.

The field `numberOfReports` should equal the total number of reports under a given employee. The number of reports is 
determined by the number of `directReports` for an employee, all of their distinct reports, and so on.

This new type should have a new REST endpoint created for it. This new endpoint should accept an `employeeId` and return
the fully filled out `ReportingStructure` for the specified `employeeId`. The values should be computed on the fly and 
will not be persisted.

1. Build class ReportingStructure w/ field employee & numReports
2. Method to find NumReports:
   1. Option 1 - DFS
   2. Option 2 - Recursive
3. Create Endpoint 

## Option 1 - DFS
Depth first search the subordinate tree, keep a list of traversed to not double count or create loops (not possible in reporting structure?)

```
List<Employee> reportQueue = employee.getReports()
Set<EmployeeId> traversed = {}
int numReports = reportQueue.size()

while (!reportQueue.isEmpty()){
   Employee emp = reportQueue.pop()
   numReports++
   traversed.add(emp.getID())
   List<Employee> reporters = emp.getReports()
   stream - 
        filter employees whose id is in traversed
        add to reportQueue  
}

return numReports
```

## Option 2 - Recursive

Base case: No subordinates - return a set with only the passed employees ID
Else: Has subordinates - return a concatenated set of getUniqueReports for all subordinates

```
private set<String> getUniqueReports(Employee employee){
    Set<String> uniqueReports = {employee.getId()}
    List<Employee> reports = employee.getReports()
    if(reports.isEmpty()){
        return uniqueReports
    }
    for subordinate in reports {
        uniqueReports.concat(subordinate.getUniqueReports())
    }

}
```

```
public getReportNum(String ID){
    Employee emp = getEmployee(ID)
    set<String> allSubordinates = getUniqueReports(emp)
    return allSubordinates.size()-1
}
```