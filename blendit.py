import re;

def create_Vertices (name, verts):
    # Create mesh and object
    me = bpy.data.meshes.new(name+'Mesh')
    ob = bpy.data.objects.new(name, me)
    ob.show_name = True
    # Link object to scene
    bpy.context.scene.objects.link(ob)
    me.from_pydata(verts, [], [])
    # Update mesh with new data
    me.update()
    return ob

vertices=[]
with open('test2', 'r') as myfile:
   for line in myfile:
   		vertices.append(eval(line))

print(vertices)

create_Vertices("test",vertices)

