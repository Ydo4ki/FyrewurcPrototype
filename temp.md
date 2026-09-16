
---
<center>Symbols</center>
---

Let $\newcommand{\qqquad}{\qquad\quad}V$ be the set of all Vals

$\forall X\in V∶X∗=\{x\in V|type(x)=X\}$  
$\forall x,y,z\in V: x(y,z)=x(y)(z)$  
$\forall x\in V∶x.y=x(Symbol\{"y"\})$

---

## Val and Type
$type(x) \in V$  
$\quad where\ x \in V$

$\forall x, y\in V:$  
$\quad x(y)=type(x)(Call\{x,y\})$

$Call\in V$  

$\forall c\in Call∗∶$  
$\quad c.val\in V$  
$\quad c.arg\in V$  
$\quad c.instance(p)\in c.val*$  
$\quad c.unpack(i)=p$  
$\qquad where\ i=c.instance(p)$  
$\forall x,y\in V∶$  
$\quad Call\{x,y\}\in Call∗$  
$\quad Call\{x,y\}.val=x$  
$\quad Call\{x,y\}.arg=y$  

## Symbol
$Symbol \in V$  
$Symbol\{"y"\}\not= Symbol\{"x"\}$

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
$\quad v.operation\in V$

$\forall x\in Vit:$  
$\quad VitInvoke.cons(x)=v$  
$\qquad where$  
$\qqquad v\in VitInvoke*$  
$\qqquad v.operation=x$

### eval

$Action\{x,s\}.value = x \land Action\{x,s\}.state = s$  
$\quad where$  
$\qquad x \in V$  
$\qquad s \in State$

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

$eval(v,r,s)=apply(v.operation,s)$  
$\quad where\ v\in VitInvoke*$

### vit-operation

$vit-operation \in V$

$vit-operation(v,r)\in V$  
$\quad where$  
$\qquad v \in Vit$

$apply(vit-operation(v,r),s) = eval(v,r,s)$

### read-operation

$read-operation \in V$

$apply(read-operation,s)=Action\{s,s\}$  
$\quad s\in V$

### write-operation

$write-operation(x) \in V$

$apply(write-operation(x),s)=Action\{unit,x\}$  

