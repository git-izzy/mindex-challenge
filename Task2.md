# Task 2

Create a new type called `Compensation` to represent an employee's compensation details. A `Compensation` should have at 
minimum these two fields: `salary` and `effectiveDate`. Each `Compensation` should be associated with a specific 
`Employee`. How that association is implemented is up to you.

Create two new REST endpoints to create and read `Compensation` information from the database. These endpoints should 
persist and fetch `Compensation` data for a specific `Employee` using the persistence layer.

1. Create `Compensation` with attributes salary, effective date, and employeeId
2. Create Compensation Repository
   1. findByEmployeeId - returns list
   2. findByEmployeeIdAndEffectiveDateBefore -returns list
3. Create endpoints
   1. Create
   2. FindAll
   3. FindMostRecent
4. Bootstrap example data
   1. Give John Lennon three compensations across different years
   2. Give Ringo Starr one compensation
