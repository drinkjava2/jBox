XCOPY "..\jbeanbox\src\main\java\javax\*.*" ".\src\main\java\javax\" /S /D /Y
XCOPY "..\jbeanbox\src\main\java\org\springframework\*.*" ".\src\main\java\org\springframework" /S /D /Y
XCOPY "..\jbeanbox\src\main\java\com\github\drinkjava2\jbeanbox\*.*" ".\src\main\java\com\github\drinkjava2\jbeanbox\" /S /D /Y
del ".\src\main\java\com\github\drinkjava2\jbeanbox\AopUtils.java"
del ".\src\main\java\com\github\drinkjava2\jbeanbox\ProxyBean.java"