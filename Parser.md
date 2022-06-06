##W trakcie przygotowywania

###Typy
- [x] ANIMO
- [x] APPLICATION
- [x] ARRAY
- [x] BEHAVIOUR
- [x] BOOL
- [x] BUTTON
- [ ] CANVAS
- [x] CANVAS_OBSERVER
- [x] CLASS
- [x] CNVLOADER
- [x] COMPLEXCONDITION
- [x] CONDITION
- [x] DATABASE
- [ ] DIALOG
- [x] DOUBLE
- [ ] EDITBOX
- [ ] EDITGROUP
- [x] EPISODE
- [x] EXPRESSION
- [ ] FIFO
- [x] FILTER
- [x] FONT
- [ ] GRBUFFER
- [x] GROUP
- [x] IMAGE
- [x] INTEGER
- [ ] INTERNET
- [ ] JOYSTICK
- [x] KEYBOARD
- [ ] LIFO
- [x] MOUSE
- [ ] MOVIE
- [x] MULTIARRAY
- [x] MUSIC
- [ ] NETCLIENT
- [ ] NETPEER
- [ ] NETSERVER
- [ ] PAINTER
- [ ] PATH
- [x] PATTERN
- [ ] RAND
- [x] SCENE
- [ ] SCROLL
- [x] SEQUENCE
- [ ] SIGNAL
- [x] SOUND
- [x] STATICFILTER
- [x] STRING
- [x] STRUCT
- [ ] SYSTEM
- [x] TEXT
- [ ] VIRTUALGRAPHICSOBJECT
- [x] TIMER
- [x] VECTOR
- [x] WORLD

###Instrucje
- [x] @BOOL
- [x] @BREAK
- [ ] @CONTINUE
- [ ] @CONV
- [ ] @CREATE
- [x] @DOUBLE
- [ ] @FOR
- [ ] @GETAPPLICATIONNAME
- [ ] @GETCURRENTSCENE
- [x] @IF
- [x] @INT
- [x] @LOOP
- [ ] @MSGBOX
- [ ] @ONEBREAK
- [x] @RETURN
- [ ] @RUNONTIMER
- [x] @STRING
- [ ] @VALUE
- [ ] @WHILE

##Disclaimer: pola, sygnały bądź typy z gwiazdką na końcu to te, których automatyczny skaner nie znalazł ani razu w skryptach gier z Reksiem

###Animo
- [ ] FILENAME
- [ ] FPS
- [ ] MONITORCOLLISION
- [ ] MONITORCOLLISIONALPHA
- [ ] ONCLICK
- [ ] ONCOLLISION
- [ ] ONCOLLISIONFINISHED*
- [ ] ONCOLLISION^param
- [ ] ONDONE*
- [ ] ONFINISHED
- [ ] ONFINISHED^param
- [ ] ONFIRSTFRAME*
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONFRAMECHANGED
- [ ] ONFRAMECHANGED^param
- [ ] ONINIT
- [ ] ONINIT^param
- [ ] ONPAUSED*
- [ ] ONRELEASE
- [ ] ONRESUMED*
- [ ] ONSIGNAL*
- [ ] ONSIGNAL^param
- [ ] ONSTARTED
- [ ] ONSTARTED^param
- [ ] PRELOAD
- [ ] PRIORITY
- [ ] RELEASE
- [ ] TOCANVAS
- [ ] VISIBLE

###Application
- [ ] AUTHOR
- [ ] BLOOMOO_VERSION
- [ ] CREATIONTIME
- [ ] DESCRIPTION
- [ ] EPISODES
- [ ] LASTMODIFYTIME
- [ ] PATH
- [ ] STARTWITH
- [ ] VERSION

###Array

###Behaviour
- [ ] CODE
- [ ] CONDITION
- [ ] DESCRIPTION

###Bool
- [ ] ONBRUTALCHANGED*
- [ ] ONBRUTALCHANGED^param
- [ ] ONCHANGED
- [ ] ONCHANGED^param
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [ ] TOINI
- [ ] VALUE

###Button
- [ ] DESCRIPTION
- [ ] DRAGGABLE
- [ ] ENABLE
- [ ] GFXONCLICK
- [ ] GFXONMOVE
- [ ] GFXSTANDARD
- [ ] ONACTION
- [ ] ONCLICKED
- [ ] ONDONE*
- [ ] ONDRAGGING*
- [ ] ONENDDRAGGING
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONPAUSED*
- [ ] ONRELEASED
- [ ] ONSIGNAL*
- [ ] ONSTARTDRAGGING
- [ ] PRIORITY
- [ ] RECT
- [ ] SNDONCLICK*
- [ ] SNDONMOVE
- [ ] SNDSTANDARD*
- [ ] VISIBLE

###CNVLoader*

###Canvas*

###Canvas_Observer
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONINITIALUPDATE*
- [ ] ONINITIALUPDATED*
- [ ] ONSIGNAL*
- [ ] ONUPDATE*
- [ ] ONUPDATED*
- [ ] ONWINDOWFOCUSOFF
- [ ] ONWINDOWFOCUSON

###Canvasobserver
Literówka => przekierować do Canvas_Observer

###Class
- [ ] BASE
- [ ] DEF

###ComplexCondition*
- [ ] CONDITION1
- [ ] CONDITION2
- [ ] DESCRIPTION
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS
- [ ] OPERATOR

###Condition
- [ ] DESCRIPTION
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS
- [ ] OPERAND1
- [ ] OPERAND2
- [ ] OPERATOR

###Database
- [ ] MODEL
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONSIGNAL*

###Dialog*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###Double
- [ ] ONBRUTALCHANGED*
- [ ] ONCHANGED*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [ ] TOINI
- [ ] VALUE

###Editbox*
- [ ] ONCHANGED*
- [ ] ONDONE*
- [ ] ONENTER*
- [ ] ONESC*
- [ ] ONFOCUSOFF*
- [ ] ONFOCUSON*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] PRIORITY*
- [ ] RECT*
- [ ] TEXT*

###Episode
- [ ] AUTHOR
- [ ] CREATIONTIME
- [ ] DESCRIPTION
- [ ] LASTMODIFYTIME
- [ ] PATH
- [ ] SCENES
- [ ] STARTWITH
- [ ] VERSION

###Expression
- [ ] OPERAND1
- [ ] OPERAND2
- [ ] OPERATOR

###Filter
- [ ] ACTION

###Font
- [ ] DEF_[family]_[style]_[size] (=> DEF(family, style, size))
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###GRBuffer*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] SOURCE*

###Group
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONSIGNAL*

###Image
- [ ] DESCRIPTION
- [ ] FILENAME
- [ ] MONITORCOLLISION
- [ ] MONITORCOLLISIONALPHA
- [ ] ONCLICK
- [ ] ONCOLLISION*
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONRELEASE*
- [ ] ONSIGNAL*
- [ ] PRELOAD
- [ ] PRIORITY
- [ ] RELEASE
- [ ] TOCANVAS
- [ ] VISIBLE

###Inertia


###Integer
- [ ] DESCRIPTION
- [ ] ONBRUTALCHANGED
- [ ] ONBRUTALCHANGED^param
- [ ] ONCHANGED
- [ ] ONCHANGED^param
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [ ] ONSIGNAL^param
- [ ] TOINI
- [ ] VALUE
- [ ] VARTYPE

###Internet*
- [ ] ONCONNECTED*
- [ ] ONDONE*
- [ ] ONDOWNLOADED*
- [ ] ONERROR*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] SERVER*
- [ ] VARSERVER*

###Joystick*
- [ ] ONBUTTONDOWN*
- [ ] ONBUTTONUP*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONMOVE*
- [ ] ONSIGNAL*
- [ ] PORT*

###Keyboard
- [ ] ONCHAR*
- [ ] ONCHAR^param
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONKEYDOWN
- [ ] ONKEYDOWN^param
- [ ] ONKEYUP
- [ ] ONKEYUP^param
- [ ] ONSIGNAL*

###Matrix
- [ ] BASEPOS
- [ ] CELLHEIGHT
- [ ] CELLWIDTH
- [ ] ONLATEST
- [ ] ONNEXT
- [ ] SIZE

###Mouse
- [ ] ONCLICK
- [ ] ONCLICK^param
- [ ] ONDBLCLICK
- [ ] ONDBLCLICK^param
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONMOVE
- [ ] ONRELEASE
- [ ] ONRELEASE^param
- [ ] ONSIGNAL*
- [ ] RAW

###MultiArray
- [ ] DIMENSIONS

###Music
- [ ] FILENAME

###NetServer*
- [ ] NETSERVER*
- [ ] ONCOMMAND*
- [ ] ONDESTROYPLAYER*
- [ ] ONNEWPLAYER*
- [ ] ONTEXT*

###NetClient*
- [ ] NETCLIENT*
- [ ] ONCOMMAND*
- [ ] ONCONNECTED*
- [ ] ONCONNECTIONCLOSED*

###NetPeer*

###Pattern
- [ ] GRIDX
- [ ] GRIDY
- [ ] HEIGHT
- [ ] LAYERS
- [ ] ONCLICK*
- [ ] ONDONE*
- [ ] ONFOCUSOFF*
- [ ] ONFOCUSON*
- [ ] ONINIT*
- [ ] ONRELEASE*
- [ ] ONSIGNAL*
- [ ] PRIORITY
- [ ] TOCANVAS
- [ ] VISIBLE
- [ ] WIDTH

###Rand

###Scene
- [ ] AUTHOR
- [ ] BACKGROUND
- [ ] COAUTHORS*
- [ ] CREATIONTIME
- [ ] DEAMON*
- [ ] DESCRIPTION
- [ ] DLLS
- [ ] LASTMODIFYTIME
- [ ] MUSIC
- [ ] ONACTIVATE*
- [ ] ONDEACTIVATE*
- [ ] ONDOMODAL*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONMUSICLOOPED*
- [ ] ONRESTART*
- [ ] ONSIGNAL*
- [ ] PATH
- [ ] VERSION

###Scroll*
- [ ] ONBOTTOM*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONLEFT*
- [ ] ONRIGHT*
- [ ] ONSIGNAL*
- [ ] ONTOP*
- [ ] VIEWPORT*

###Sequence
- [ ] FILENAME
- [ ] ONDONE*
- [ ] ONFINISHED
- [ ] ONFINISHED^param
- [ ] ONINIT
- [ ] ONSIGNAL*
- [ ] ONSTARTED
- [ ] ONSTARTED^param
- [ ] VISIBLE

###Sound
- [ ] DESCRIPTION
- [ ] FILENAME
- [ ] FLUSHAFTERPLAYED
- [ ] ONDONE*
- [ ] ONFINISHED
- [ ] ONINIT
- [ ] ONRESUMED*
- [ ] ONSIGNAL*
- [ ] ONSTARTED
- [ ] PRELOAD
- [ ] RELEASE

###StaticFilter
- [ ] ACTION

###String
- [ ] DESCRIPTION
- [ ] ONBRUTALCHANGED
- [ ] ONBRUTALCHANGED^param
- [ ] ONCHANGED
- [ ] ONCHANGED^param
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [ ] TOINI
- [ ] VALUE

###Struct
- [ ] FIELDS
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###System*

###Text
- [ ] FONT
- [ ] HJUSTIFY
- [ ] HYPERTEXT*
- [ ] MONITORCOLLISION
- [ ] MONITORCOLLISIONALPHA
- [ ] ONCOLLISION*
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONSIGNAL*
- [ ] PRIORITY
- [ ] RECT
- [ ] TEXT
- [ ] TOCANVAS
- [ ] VISIBLE
- [ ] VJUSTIFY

###Timer
- [ ] ELAPSE
- [ ] ENABLED
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONSIGNAL*
- [ ] ONTICK
- [ ] ONTICK^param
- [ ] TICKS

###Vector
- [ ] SIZE
- [ ] VALUE

###VirtualGraphicsObject*
- [ ] ASBUTTON*
- [ ] MASK*
- [ ] MONITORCOLLISION*
- [ ] MONITORCOLLISIONALPHA*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] PRIORITY*
- [ ] SOURCE*
- [ ] TOCANVAS*
- [ ] VISIBLE*

###World
- [ ] FILENAME
