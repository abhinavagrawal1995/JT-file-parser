import bpy

layers=[]
with open('abi/layers', 'r') as myfile2:
   for line in myfile2:
   		layers.append(line)
del layers[0]


for l in layers:
	verts=[]
	with open('abi/vert_'+l.rstrip('\n'), 'r') as myfile1:
	   for line in myfile1:
	   		verts.append(eval(line))
	faces=[]
	with open('abi/face_'+l.rstrip('\n'), 'r') as myfile2:
	   for line in myfile2:
	   		faces.append(eval(line))
	mesh_data = bpy.data.meshes.new(l.rstrip('\n')+"_data")  
	mesh_data.from_pydata(verts, [], faces)
	mesh_data.update()
	obj = bpy.data.objects.new(l.rstrip('\n'), mesh_data)  
	scene = bpy.context.scene    
	scene.objects.link(obj)    
	obj.select = True  

print("done")
