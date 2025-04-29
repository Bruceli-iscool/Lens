import SwiftUI
import PhotosUI

struct ContentView: View {
    @State private var selectedItem: PhotosPickerItem?
    @State private var selectedImage: Image?
    @State private var newWindow: NSWindow?
    

    var body: some View {
        VStack {
            if let selectedImage {
                selectedImage
                    .resizable()
                    .scaledToFit()
                    .frame(width: 300, height: 300)
            }
            Text("Welcome To Lens").padding(.top).bold()
            PhotosPicker("Open Image", selection: $selectedItem, matching: .images)
                .padding(.bottom)
        }
        .onChange(of: selectedItem) { newItem in
            Task {
                if let data = try? await newItem?.loadTransferable(type: Data.self) {

                    if let nsImage = NSImage(data: data) {
                        selectedImage = Image(nsImage: nsImage)
                        openNewWindow(with: nsImage)
                    }

                }
            }
        }

    }
    
    func closeOriginalWindow() {
        if let window = NSApplication.shared.keyWindow {
            window.close()
        }
    }
    
    func openNewWindow(with image: NSImage) {
        closeOriginalWindow()
        let screenFrame = NSScreen.main?.frame ?? NSMakeRect(0, 0, 1440, 900)
        
        let newWindow = NSWindow(
            contentRect: screenFrame,
            styleMask: [.titled, .closable, .resizable],
            backing: .buffered,
            defer: false
        )
        newWindow.title = "Lens Editor"
        
        let newWindowView = EditorView(image: Image(nsImage: image))
        
        newWindow.contentView = NSHostingView(rootView: newWindowView)
        newWindow.makeKeyAndOrderFront(nil)
        
        self.newWindow = newWindow
    }
}

struct EditorView: View {
    var image: Image
    
    var body: some View {
        VStack {
            Button("Exposure") {
                // Placeholder for future feature
            }
            image
                .resizable()
                .scaledToFit()
        }
        .padding()
    }
}

#Preview {
    EditorView(image: Image("IMG_0129"))
}
