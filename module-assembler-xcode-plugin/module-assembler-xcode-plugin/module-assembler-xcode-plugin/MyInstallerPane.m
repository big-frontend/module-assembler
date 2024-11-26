//
//  MyInstallerPane.m
//  module-assembler-xcode-plugin
//
//  Created by hawks.jamesf on 2024/7/3.
//

#import "MyInstallerPane.h"

@implementation MyInstallerPane

- (NSString *)title
{
    return [[NSBundle bundleForClass:[self class]] localizedStringForKey:@"PaneTitle" value:nil table:nil];
}

@end
