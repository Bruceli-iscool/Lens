//
//  LensApp.swift
//  Lens
//
//  Created by Bruce Li on 4/27/25.
//

import SwiftUI

@main
struct LensApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
