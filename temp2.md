$$$
S=substitutor
$$$
$$$
K=const
$$$
$$$
I=mirror
$$$
$$$
T=K
$$$
$$$
F=S(K)
$$$

<br>

$$\huge\text{Unit}$$

$$$\\
UnitCH(c)=eq(c.arg,Symbol\{"cons"\})(c.instance(\_),unspecified)
\\$$$

$$$\\
inst=S(I,K(Symbol\{"instance"\}))
\\$$$
$$$\\
inst0=S(inst,K(\_))
\\$$$
$$$\\
ga=S(I,Symbol\{"arg"\})
\\$$$
$$$\\
eqCons=S(S(eq,ga), K.cons)
\\$$$
$$$\\
UnitCH(c)=eqCons(c)(inst0(c))(K(unspecified)(c))
\\$$$
$$$\\
UnitCH(c)=S(eqCons, inst0)(c)(K(unspecified)(c))
\\$$$
$$$\\
UnitCH=S(S(eqCons, inst0), K(unspecified))
\\$$$
$$$\\
UnitCH=S(S(S(S(eq,S(I,Symbol\{"arg"\})), K.cons), S(S(I,K(Symbol\{"instance"\})),K(\_))), K(unspecified))
\\$$$
$$$\\
Unit=telephonist(UnitCH)
\\$$$
$$$\\
unit=Unit.cons
\\$$$
<br>
<br>
$$\huge\text{Bool}$$
$$$
\newcommand{s}[1]{ Symbol\{"#1"\} }
BoolCH(c)=eq(c.arg,\s{true})(c.instance(T))
(eq(c.arg,Symbol\{"false"\})(c.instance(F))
(and(eq(type(c.arg),Call),eq(type(c.arg.val),c.val))
(eq(c.arg.arg,Symbol\{"if"\})(c.unpack(c.arg.val))\not U)))
$$$
$$$
Bool=telephonist(BoolCH)
$$$
$$$
true=Bool.true
$$$
$$$
false=Bool.false
$$$