import random as r

def generateTasks():
    tasks = []
    for task in range(0, numTasks):
        period = periodArr[task]
        wcet = wcetArr[r.randint(0,1)]
        deadline = possibleDeadlinesPerTask[task][r.randint(0,2)]
        print("New Task:")
        print(f"t{task}: {wcet} {period} {deadline} => {deadline - period - wcet > 0}")
        tasks.append([wcet, period, deadline])
    
    return tasks
        
def checkTasks(taskset):
    for i in range(0, len(taskset)):
        print(f"{taskset[i][2] - taskset[i][1] - taskset[i][0] < 0}")
        if (taskset[i][2] - taskset[i][1] - taskset[i][0] < 0):
            return False
        
    return True
        


randomSeed = int(input("SEED Value: "))
r.seed(randomSeed)

# Trying to make the taskset schedulable...
mult = 450
periodDifference = 8
numPossibleDeadlinesPerTask = 4
numTasks = int(input("Num tasks in taskset: "))
wcetArr = [r.randint(1, 2) for i in range(0, numTasks)]
# wcetArr = [1, 2]

periodArr = [i*periodDifference for i in range(0, numTasks)]
possibleDeadlinesPerTask = [[periodArr[i] + wcetArr[i] + j for j in range(1, periodDifference)] for i in range(0, len(periodArr))]

for i in range(0, len(periodArr)):
    print(periodArr[i])
    
for i in range(0, len(periodArr)):
    print("Values for i = " + str(i))
    for j in range(0, numPossibleDeadlinesPerTask):
        print(possibleDeadlinesPerTask[i][j])


currTasks = generateTasks()
satisfied = False
while (satisfied == False):
    satisfied = checkTasks(currTasks)
    currTasks = generateTasks()
    randomSeed += 1
    r.seed(randomSeed)
    
fileName = f"0{numTasks}"
if (numTasks > 9):
    fileName = f"{numTasks}"            
with open("output/taskSet-" + f"{fileName}.txt", 'w') as output_file:
    [ output_file.write(f"t{j} {currTasks[j][0]} {currTasks[j][1]} {currTasks[j][2]}\n") for j in range(0, numTasks) ]

# for i in range(0, 10):
#     pUpper = 100000
#     pLower = 9999
    
#     for tasksetSize in range (2, 25):
#         pUpper += (i+1) * mult
#         pLower -= (i+1) * mult
#         if (pLower <= 0):
#             pLower *= -1
        
#         currTasks = []    
#         # j = 0
        
#         for task in range(0, tasksetSize):
#             period = r.randint(pLower, pUpper)
#             wcet = r.randint(1, period)
#             # deadline = r.randint(wcet, pUpper)    
#             deadline = r.randint(wcet, (wcet + r.randint(0, pUpper)))
#             print("New Task:")
#             print(f"t{tasksetSize}-{i}: {wcet} {period} {deadline}")
#             currTasks.append([wcet, period, deadline])
# #            j += 1
#         fileName = f"0{tasksetSize}{i}"
#         if (tasksetSize > 9):
#             fileName = f"{tasksetSize}{i}"            
#         with open("output/taskSet-" + f"{fileName}.txt", 'w') as output_file:
#             [ output_file.write(f"t{j} {currTasks[j][0]} {currTasks[j][1]} {currTasks[j][2]}\n") for j in range(0, tasksetSize) ]