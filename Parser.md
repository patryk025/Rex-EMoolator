###Typy
- [x] ANIMO
- [x] APPLICATION
- [x] ARRAY
- [x] BEHAVIOUR
- [x] BOOL
- [x] BUTTON
- [ ] CANVAS
- [x] CANVAS_OBSERVER
- [ ] CLASS
- [ ] CNVLOADER
- [x] COMPLEXCONDITION
- [x] CONDITION
- [x] DATABASE
- [ ] DIALOG
- [ ] DOUBLE
- [ ] EDITBOX
- [ ] EDITGROUP
- [x] EPISODE
- [ ] EXPRESSION
- [ ] FIFO
- [ ] FILTER
- [ ] FONT
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
- [ ] MULTIARRAY
- [ ] MUSIC
- [ ] NETCLIENT
- [ ] NETPEER
- [ ] NETSERVER
- [ ] PAINTER
- [ ] PATH
- [ ] PATTERN
- [ ] RAND
- [x] SCENE
- [ ] SCROLL
- [x] SEQUENCE
- [ ] SIGNAL
- [x] SOUND
- [ ] STATICFILTER
- [x] STRING
- [x] STRUCT
- [ ] SYSTEM
- [ ] TEXT
- [ ] VIRTUALGRAPHICSOBJECT
- [x] TIMER
- [ ] VECTOR
- [ ] WORLD

###Instrucje
- [x] @BOOL
- [ ] @BREAK
- [ ] @CONTINUE
- [ ] @CONV
- [ ] @CREATE
- [x] @DOUBLE
- [ ] @FOR
- [ ] @GETAPPLICATIONNAME
- [ ] @GETCURRENTSCENE
- [x] @IF
- [x] @INT
- [ ] @LOOP
- [ ] @MSGBOX
- [ ] @ONEBREAK
- [x] @RETURN
- [ ] @RUNONTIMER
- [x] @STRING
- [ ] @VALUE
- [ ] @WHILE

##Disclaimer: pola, sygnały bądź typy z gwiazdką na końcu to te, których automatyczny skaner nie znalazł ani razu w skryptach gier z Reksiem

###Animo
- [x] FILENAME
- [x] FPS
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [ ] ONCLICK
- [x] ONCOLLISION
- [ ] ONCOLLISIONFINISHED*
- [ ] ONCOLLISION^param
- [ ] ONDONE*
- [x] ONFINISHED
- [x] ONFINISHED^param
- [ ] ONFIRSTFRAME*
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [x] ONFRAMECHANGED
- [x] ONFRAMECHANGED^param
- [x] ONINIT
- [ ] ONINIT^param
- [ ] ONPAUSED*
- [ ] ONRELEASE
- [ ] ONRESUMED*
- [ ] ONSIGNAL*
- [ ] ONSIGNAL^param
- [x] ONSTARTED
- [x] ONSTARTED^param
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE

###Application
- [x] AUTHOR
- [ ] BLOOMOO_VERSION
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] EPISODES
- [x] LASTMODIFYTIME
- [x] PATH
- [x] STARTWITH
- [x] VERSION

###Array

###Behaviour
- [x] CODE
- [x] CONDITION
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
- [x] VALUE

###Button
- [ ] DESCRIPTION
- [x] DRAGGABLE
- [x] ENABLE
- [x] GFXONCLICK
- [x] GFXONMOVE
- [x] GFXSTANDARD
- [x] ONACTION
- [ ] ONCLICKED
- [ ] ONDONE*
- [ ] ONDRAGGING*
- [ ] ONENDDRAGGING
- [x] ONFOCUSOFF
- [x] ONFOCUSON
- [ ] ONINIT
- [ ] ONPAUSED*
- [ ] ONRELEASED
- [ ] ONSIGNAL*
- [ ] ONSTARTDRAGGING
- [ ] PRIORITY
- [x] RECT
- [ ] SNDONCLICK*
- [ ] SNDONMOVE
- [ ] SNDSTANDARD*
- [x] VISIBLE

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
- [x] CONDITION1
- [x] CONDITION2
- [ ] DESCRIPTION
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS
- [x] OPERATOR

###Condition
- [ ] DESCRIPTION
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS
- [x] OPERAND1
- [x] OPERAND2
- [x] OPERATOR

###Database
- [x] MODEL
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
- [x] AUTHOR
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] LASTMODIFYTIME
- [ ] PATH
- [x] SCENES
- [x] STARTWITH
- [x] VERSION

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
- [x] FILENAME
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [ ] ONCLICK
- [ ] ONCOLLISION*
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONRELEASE*
- [ ] ONSIGNAL*
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE

###Inertia


###Integer
- [ ] DESCRIPTION
- [x] ONBRUTALCHANGED
- [ ] ONBRUTALCHANGED^param
- [ ] ONCHANGED
- [ ] ONCHANGED^param
- [ ] ONDONE*
- [ ] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [ ] ONSIGNAL^param
- [x] TOINI
- [x] VALUE
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
- [x] AUTHOR
- [x] BACKGROUND
- [ ] COAUTHORS*
- [x] CREATIONTIME
- [ ] DEAMON*
- [x] DESCRIPTION
- [ ] DLLS
- [x] LASTMODIFYTIME
- [ ] MUSIC
- [ ] ONACTIVATE*
- [ ] ONDEACTIVATE*
- [ ] ONDOMODAL*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONMUSICLOOPED*
- [ ] ONRESTART*
- [ ] ONSIGNAL*
- [x] PATH
- [x] VERSION

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
- [x] FILENAME
- [ ] ONDONE*
- [x] ONFINISHED
- [x] ONFINISHED^param
- [x] ONINIT
- [ ] ONSIGNAL*
- [ ] ONSTARTED
- [ ] ONSTARTED^param
- [ ] VISIBLE

###Sound
- [ ] DESCRIPTION
- [x] FILENAME
- [x] FLUSHAFTERPLAYED
- [ ] ONDONE*
- [x] ONFINISHED
- [ ] ONINIT
- [ ] ONRESUMED*
- [ ] ONSIGNAL*
- [x] ONSTARTED
- [x] PRELOAD
- [x] RELEASE

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
- [x] TOINI
- [x] VALUE

###Struct
- [x] FIELDS
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
