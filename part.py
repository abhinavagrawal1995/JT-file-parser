import bpy  

verts=[]
with open('abi/vert_butterflyvalveopt#valve_sub_assy#shaft#shaft_SOLID_SOLIDS', 'r') as myfile1:
   for line in myfile1:
   		verts.append(eval(line))
faces=[]
with open('abi/face_butterflyvalveopt#valve_sub_assy#shaft#shaft_SOLID_SOLIDS', 'r') as myfile2:
   for line in myfile2:
   		faces.append(eval(line))

mesh_data = bpy.data.meshes.new("cube_mesh_data")  
mesh_data.from_pydata(verts, [], faces)  
mesh_data.update()

obj = bpy.data.objects.new("My_Object", mesh_data)  

scene = bpy.context.scene    
scene.objects.link(obj)    
obj.select = True  



print("done") 