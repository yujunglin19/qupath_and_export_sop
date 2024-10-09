import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay

def imageData = getCurrentImageData()

def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
//print(name)
def tmp = name.split(' - ')
//print(tmp)
def img_name = tmp[0]
//print(img_name)

String folderOutput = buildFilePath(PROJECT_BASE_DIR, 'export')
mkdirs(folderOutput)

String img_folder_path = buildFilePath(folderOutput, GeneralTools.getNameWithoutExtension(getProjectEntry().getImageName()))
print(img_folder_path)
mkdirs(img_folder_path)

String og_Output = buildFilePath(img_folder_path, img_name + '_organoid.tif')
print(og_Output)

double downsample = 1

// Organoid mask
def og_label = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.BLACK) //
    .downsample(downsample) // server resolution
    .addLabel('Organoid', 1) // output labels
    .lineThickness(3)
    .multichannelOutput(false)
    .build()

writeImage(og_label, og_Output)

print('organoid - done')

String nr_Output = buildFilePath(img_folder_path, img_name + '_NR.tif')
print(nr_Output)

// NR mask
def nr_label = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.BLACK) //
    .downsample(downsample) // server resolution
    .addLabel('NR', 1) // output labels
    .lineThickness(3)
    .multichannelOutput(false)
    .build()

writeImage(nr_label, nr_Output)

print('NR - done')

String rpe_Output = buildFilePath(img_folder_path, img_name + '_RPE.tif')
print(rpe_Output)

//double downsample = 1

// RPE mask
def rpe_label = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.BLACK) //
    .downsample(downsample) // server resolution
    .addLabel('RPE', 1) // output labels
    .lineThickness(3)
    .multichannelOutput(false)
    .build()

writeImage(rpe_label, rpe_Output)

print('RPE - done')

// export all ROIs
def annotations = getAnnotationObjects()
String anno_path = buildFilePath(img_folder_path, GeneralTools.getNameWithoutExtension(getProjectEntry().getImageName()) + '.geojson')
print(anno_path)
// 'FEATURE_COLLECTION' is standard GeoJSON format for multiple objects
exportObjectsToGeoJson(annotations, anno_path, "FEATURE_COLLECTION")

print('ROI - done')

// downsample factor: change to desired value
downsample = 20

// obtain current viewer and image data
def viewer = getCurrentViewer()
print(viewer)
//def imageData = getCurrentImageData()
//print(imageData)

// create downsample rendered image
def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    .layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
    .lineThickness(3) // might cause error, remove if needed
    .build()

// save image
writeImage(server, img_path)

print('Overview - done')
print('Export Complete')
