

## Vit

$VitVal,VitVar,VitCall,VitInvoke\in V$

$Vit=VitVal*⊔VitVar*⊔VitCall*⊔VitInvoke*$

$∀v\in VitVal*:$  
$\quad v.value\in V$

$VitVal.cons(x)=v$  
$\quad where$  
$\qquad v \in VitVal*$  
$\qquad v.value=x$

$VitVar.cons\in VitVar*$  
$\forall x,y\in VitVar*:$  
$\quad x=y$

$\forall v\in VitCall*:$  
$\quad v.func\in Vit$  
$\quad v.arg\in Vit$

$\forall x,y\in Vit:$  
$\quad VitCall.cons(x,y)=v$  
$\qquad where$  
$\qqquad v\in VitCall*$  
$\qqquad v.func=x$  
$\qqquad v.arg=y$

$\forall v\in VitInvoke*:$  
$\quad v.operation\in Vit$

$\forall x\in Vit:$  
$\quad VitInvoke.cons(x)=v$  
$\qquad where$  
$\qqquad v\in VitInvoke*$  
$\qqquad v.operation=x$

### eval


Let $State$ be the set of all states  
$Action \notin V$  
$unit \in V$

$Action\{x,s\}.value = x \land Action\{x,s\}.state = s$  
$\quad where\ x \in V \land s \in State$

$eval(v,r,s)\ = Action\{x,s^1\}$  
$\quad where$  
$\qquad v\in Vit$  
$\qquad r\in V$  
$\qquad s\in State$  
$\qquad x\in V$  
$\qquad s^1\in State$

$eval(v,r,s)=Action\{v.value,s\}$  
$\quad where\ v\in VitVal*$

$eval(v,r,s)=Action\{r,s\}$  
$\quad where\ v\in VitVar*$

$eval(v,r,s)=Action\{fa.value(aa.value), aa.state\}$  
$\quad where$  
$\qquad v\in VitCall*$  
$\qquad fa=eval(v.func,r,s)$  
$\qquad aa=eval(v.arg,r,fa.state)$

$eval(v,r,s)=apply(ac.value,ac.state)$  
$\quad where$  
$\qquad v\in VitInvoke*$  
$\qquad ac = eval(v.operation,r,s)$

$apply(o,s) = Action(x,s^1)$  
$\quad where$  
$\qquad o\in V$  
$\qquad s\in State$  
$\qquad x\in V$  
$\qquad s^1\in State$

### operation

$operation \in V$

$operation(v,r)\in V$  
$\quad where$  
$\qquad v \in Vit$

$apply(operation(v,r),s) = eval(v,r,s)$

### Atomic Objects

$LaserPointer \in V$  
$\forall lp \in LaserPointer*$  
$\quad pl.read \in V$  
$\quad pl.write \in V$  
$\quad pl.write(v) \in V$  
$\qquad where\ v \in V$

$new \in V$  
$new(v) \in V$  
$\quad where\ v \in V$

$apply(new(v),s)=Action\{lp,s[lp \mapsto v]\}$  
$\quad where$  
$\qquad lp \in LaserPointer*$  
$\qquad lp \notin dom(s)$  
$\qquad apply(lp.read, s^1) =Action\{s^1(lp),s^1\}$
$\qquad apply(lp.write(v), s^1) =Action\{unit,s^1[lp \mapsto v]\}$  
$\qquad s^1 \in State$
