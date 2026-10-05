
---
<center>Symbols</center>
---

Let $\newcommand{\qqquad}{\qquad\quad}V$ be the set of all Vals

$\forall X\in V∶X∗=\{x\in V|type(x)=X\}$  
$\forall x,y,z\in V: x(y,z)=x(y)(z)$  
$\forall x\in V∶x.y=x(Symbol\{"y"\})$

---

## Val and Type
$type \in V$  
$type(x) \in V$  
$\quad where\ x \in V$

$Call\in V$

$\forall x\in V:$  
$\quad x(y)=type(x)(Call\{x,y\})$  
$\qquad where$  
$\qqquad Call\{x,y\}\in Call∗$  
$\qqquad Call\{x,y\}.val=x$  
$\qqquad Call\{x,y\}.arg=y$  
$\qqquad Call\{x,y\}.instance(p)=i$  
$\qqquad Call\{x,y\}.unpack(i)=p$  
$\qqquad i\in x*$  
$\qqquad Call\{x,y\}\ is\ initially\ private\ to\ the\ execution\ of\ type(x)$


$Telephonist^n \in V$  
$Telephonist^n \in Telephonist^{n+1}*$  
$\quad where\ n \in \mathbb{Z}$

$telephonist \in V$  
$telephonist(ch)=t$  
$\quad where$  
$\qquad t \in Telephonist^0*$  
$\qquad v \in V$  
$\qquad t(v)=ch(Call\{t,v\})$

## Symbol
$Symbol \in V$  
$Symbol\{"y"\}\not= Symbol\{"x"\}$

### Auxiliary values

$substitutor, const \in Telephonist^0*$  
$substitutor(x,y,z)=(x(z))(y(z))$  
$const(x,y)=x$  
$mirror=substitutor(const,const)$  
$eq(x,y)=$  
$\quad T\ if\ x = y$  
$\quad F\ otherwise$

$\not \text{U}$
