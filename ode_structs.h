// Portions of this file are derived from the Open Dynamics Engine (ODE), version 0.039
// Used under the terms of the ODE BSD-style license.

/*
This is the BSD-style license for the Open Dynamics Engine
----------------------------------------------------------

Open Dynamics Engine
Copyright (c) 2001-2003, Russell L. Smith.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

Neither the names of ODE's copyright owner nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

// typedefs from ODE 0.039
// common.h
typedef double dReal;

typedef dReal dVector3[4];
typedef dReal dVector4[4];
typedef dReal dMatrix3[4*3];
typedef dReal dMatrix4[4*4];
typedef dReal dMatrix6[8*6];
typedef dReal dQuaternion[4];

// Forward declarations
struct dxWorld;
struct dxBody;
struct dxGeom;
struct dxJoint;
struct dxJointNode;
struct dxSpace;
struct dJointFeedback;

// mass.h
struct dMass {
  dReal mass;
  dVector4 c;
  dMatrix3 I;
};

// objects.h
struct dBase {}; // there was only virtual constructor and destructor, omitted here

struct dObject : public dBase {
  dxWorld *world;
  dObject *next;
  dObject **tome;
  void *userdata;
  int tag;
};

struct dxBody : public dObject {
  dxJointNode *firstjoint;
  int flags;
  dxGeom *geom;
  dMass mass;
  dMatrix3 invI;
  dReal invMass;
  dVector3 pos;
  dQuaternion q;
  dMatrix3 R;
  dVector3 lvel,avel;
  dVector3 facc,tacc;
  dVector3 finite_rot_axis;
};


struct dxWorld : public dBase {
  dxBody *firstbody;
  dxJoint *firstjoint;
  int nb,nj;
  dVector3 gravity;
  dReal global_erp;
  dReal global_cfm;
};

// collision_kernel.h
struct dxGeom : public dBase {
  int type;
  int gflags;
  void *data;
  dxBody *body;
  dxGeom *body_next;
  dReal *pos;
  dReal *R;

  dxGeom *next;
  dxGeom **tome;
  dxSpace *parent_space;
  dReal aabb[6];
  unsigned long category_bits,collide_bits;
};

// joint.h
struct dxJointNode {
  dxJoint *joint;
  dxBody *body;
  dxJointNode *next;
};

struct dxJoint : public dObject {
  struct Info1 {
    int m,nub;
  };

  struct Info2 {
    dReal fps,erp;
    dReal *J1l,*J1a,*J2l,*J2a;
    int rowskip;
    dReal *c,*cfm;
    dReal *lo,*hi;
    int *findex;
  };

  typedef void init_fn (dxJoint *joint);
  typedef void getInfo1_fn (dxJoint *joint, Info1 *info);
  typedef void getInfo2_fn (dxJoint *joint, Info2 *info);
  struct Vtable {
    int size;
    init_fn *init;
    getInfo1_fn *getInfo1;
    getInfo2_fn *getInfo2;
    int typenum;
  };

  Vtable *vtable;
  int flags;
  dxJointNode node[2];
  dJointFeedback *feedback;
};

// common.h
typedef struct dJointFeedback {
  dVector3 f1;
  dVector3 t1;
  dVector3 f2;
  dVector3 t2;
} dJointFeedback;

// collision_kernel.h
struct dxSpace : public dxGeom {
  int count;
  dxGeom *first;
  int cleanup;

  int current_index;
  dxGeom *current_geom;
  int lock_count;
};
