#即刻点赞

##分析

1.未点赞状态点击（图标有一个微弱的回弹效果，数字是没有的）:

图标:灰色100%(按下)->灰色80%(松开)->红色80%/alp80%->红色100%(还有一个约90%大小的红圈扩散消失)

数字:数字增加，旧数字向上淡出新数字从下淡入。(临界情况有增加数位和多数位进位)

2.点赞状态点击:

图标:红色100%(按下)->红色80%(松开)->灰色80%/alp80%->灰色100%

数字:数字减小，旧数字向下淡出新数字从上淡入。(临界情况有减少数位和多数位减?位)

